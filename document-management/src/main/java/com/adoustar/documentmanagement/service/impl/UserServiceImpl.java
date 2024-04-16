package com.adoustar.documentmanagement.service.impl;

import com.adoustar.documentmanagement.cache.CacheStore;
import com.adoustar.documentmanagement.domain.RequestContext;
import com.adoustar.documentmanagement.domain.dto.User;
import com.adoustar.documentmanagement.entity.ConfirmationEntity;
import com.adoustar.documentmanagement.entity.CredentialEntity;
import com.adoustar.documentmanagement.entity.RoleEntity;
import com.adoustar.documentmanagement.entity.UserEntity;
import com.adoustar.documentmanagement.enums.Authority;
import com.adoustar.documentmanagement.enums.EventType;
import com.adoustar.documentmanagement.enums.LoginType;
import com.adoustar.documentmanagement.event.UserEvent;
import com.adoustar.documentmanagement.exception.ApiException;
import com.adoustar.documentmanagement.repository.ConfirmationRepository;
import com.adoustar.documentmanagement.repository.CredentialRepository;
import com.adoustar.documentmanagement.repository.RoleRepository;
import com.adoustar.documentmanagement.repository.UserRepository;
import com.adoustar.documentmanagement.service.UserService;
import dev.samstevens.totp.code.CodeGenerator;
import dev.samstevens.totp.code.CodeVerifier;
import dev.samstevens.totp.code.DefaultCodeGenerator;
import dev.samstevens.totp.code.DefaultCodeVerifier;
import dev.samstevens.totp.time.SystemTimeProvider;
import dev.samstevens.totp.time.TimeProvider;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Map;

import static com.adoustar.documentmanagement.utils.UserUtil.*;
import static org.apache.commons.lang3.StringUtils.EMPTY;

@Service
@Transactional(rollbackOn = Exception.class)
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final CredentialRepository credentialRepository;
    private final ConfirmationRepository confirmationRepository;
    private final ApplicationEventPublisher publisher;
    private final CacheStore<String, Integer> userCache;

    private final BCryptPasswordEncoder encoder;

    @Override
    public void createUser(String firstName, String lastName, String email, String password) {
        var userEntity = userRepository.save(createNewUser(firstName, lastName, email));
        var credentialEntity  = new CredentialEntity(encoder.encode(password), userEntity);
        credentialRepository.save(credentialEntity);
        var confirmationEntity = new ConfirmationEntity(userEntity);
        confirmationRepository.save(confirmationEntity);
        publisher.publishEvent(new UserEvent(userEntity, EventType.REGISTRATION, Map.of("key", confirmationEntity.getKey())));
    }

    @Override
    public RoleEntity getRoleName(String name) {
        var role = roleRepository.findByNameIgnoreCase(name);
        return role.orElseThrow(() -> new ApiException("Role not found"));
    }

    @Override
    public void verifyAccountKey(String key) {
        var confirmationEntity = getUserConfirmation(key);
        var userEntity = getUserEntityByEmail(confirmationEntity.getUserEntity().getEmail());
        userEntity.setEnabled(true);
        userRepository.save(userEntity);
        confirmationRepository.delete(confirmationEntity);
    }

    @Override
    public void updateLoginAttempt(String email, LoginType loginType) {
        var userEntity = getUserEntityByEmail(email);
        RequestContext.setUserId(userEntity.getId());
        switch (loginType) {
            case LOGIN_ATTEMPT -> {
                if (userCache.get(userEntity.getEmail()) == null) {
                    userEntity.setLoginAttempts(0);
                    userEntity.setAccountNonLocked(true);
                }
                userEntity.setLoginAttempts(userEntity.getLoginAttempts() + 1);
                userCache.put(userEntity.getEmail(), userEntity.getLoginAttempts());
                if (userCache.get(userEntity.getEmail()) > 5) {
                    userEntity.setAccountNonLocked(false);
                }
            }
            case LOGIN_SUCCESS -> {
                userEntity.setAccountNonLocked(true);
                userEntity.setLoginAttempts(0);
                userEntity.setLastLogin(LocalDateTime.now());
                userCache.evict(userEntity.getEmail());
            }
        }
        userRepository.save(userEntity);
    }

    @Override
    public User getUserByUserId(String userId) {
        UserEntity userEntity = userRepository.findUserEntityByUserId(userId).orElseThrow(() -> new ApiException("User not found"));
        return fromUserEntity(userEntity, userEntity.getRole(), getUserCredentialById(userEntity.getId()));
    }

    @Override
    public User getUserByEmail(String email) {
        UserEntity userEntity = getUserEntityByEmail(email);
        return fromUserEntity(userEntity, userEntity.getRole(), getUserCredentialById(userEntity.getId()));
    }


    @Override
    public CredentialEntity getUserCredentialById(Long userId) {
        var credentialById = credentialRepository.getCredentialsByUserEntityId(userId);
        return credentialById.orElseThrow(() -> new ApiException("Unable to find user credential."));
    }

    @Override
    public User setUpMfa(Long userId) {
        var userEntity = getUserEntityById(userId);
        var codeSecret = qrCodeSecret.get();
        userEntity.setQrCodeImageUri(qrCodeImageUri.apply(userEntity.getEmail(), codeSecret));
        userEntity.setQrCodeSecret(codeSecret);
        userEntity.setMfa(true);
        userRepository.save(userEntity);
        return fromUserEntity(userEntity, userEntity.getRole(), getUserCredentialById(userEntity.getId()));
    }

    @Override
    public User cancelMfa(Long userId) {
        var userEntity = getUserEntityById(userId);
        userEntity.setQrCodeImageUri(EMPTY);
        userEntity.setQrCodeSecret(EMPTY);
        userEntity.setMfa(false);
        userRepository.save(userEntity);
        return fromUserEntity(userEntity, userEntity.getRole(), getUserCredentialById(userEntity.getId()));
    }

    @Override
    public User verifyQrCode(String userId, String qrCode) {
        var userEntity = getUserEntityByUserId(userId);
        verifyCode(qrCode, userEntity.getQrCodeSecret());
        return fromUserEntity(userEntity, userEntity.getRole(), getUserCredentialById(userEntity.getId()));
    }

    private boolean verifyCode(String qrCode, String qrCodeSecret) {
        TimeProvider timeProvider = new SystemTimeProvider();
        CodeGenerator codeGenerator = new DefaultCodeGenerator();
        CodeVerifier codeVerifier = new DefaultCodeVerifier(codeGenerator, timeProvider);
        if (codeVerifier.isValidCode(qrCodeSecret, qrCode)) {
            return true;
        } else {
            throw new ApiException("Invalid QR Code. Please try again.");
        }
    }

    private UserEntity getUserEntityByUserId(String userId) {
        return userRepository.findUserEntityByUserId(userId).orElseThrow(() -> new ApiException("User not found"));
    }

    private UserEntity getUserEntityById(Long userId) {
        return userRepository.findById(userId).orElseThrow(() -> new ApiException("User not found"));
    }

    private UserEntity getUserEntityByEmail(String email) {
        var userByEmail = userRepository.findByEmailIgnoreCase(email);
        return userByEmail.orElseThrow(() -> new ApiException("User not found"));
    }

    private ConfirmationEntity getUserConfirmation(String key) {
        return confirmationRepository.findByKey(key).orElseThrow(() -> new ApiException("Confirmation key not found"));
    }

    private UserEntity createNewUser(String firstName, String lastName, String email) {
        var role = getRoleName(Authority.USER.name());
        return createUserEntity(firstName, lastName, email, role);
    }
}

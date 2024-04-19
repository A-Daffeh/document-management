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
import com.adoustar.documentmanagement.validation.UserValidation;
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
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.function.BiFunction;

import static com.adoustar.documentmanagement.constant.Constant.NINETY_DAYS;
import static com.adoustar.documentmanagement.constant.Constant.PHOTO_DIRECTORY;
import static com.adoustar.documentmanagement.utils.UserUtil.*;
import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;
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

    @Override
    public void resetPassword(String email) {
        var user = getUserEntityByEmail(email);
        var confirmation = getUserConfirmation(user);

        if (confirmation != null) {
            publisher.publishEvent(new UserEvent(user, EventType.RESET_PASSWORD, Map.of("key", confirmation.getKey())));
        } else {
            var confirmationEntity = new ConfirmationEntity(user);
            confirmationRepository.save(confirmationEntity);
            publisher.publishEvent(new UserEvent(user, EventType.RESET_PASSWORD, Map.of("key", confirmationEntity.getKey())));
        }
    }

    @Override
    public User verifyPasswordKey(String token) {
        var confirmationEntity = getUserConfirmation(token);
        if (confirmationEntity == null) { throw new ApiException("Unable to find token"); }
        var userEntity = getUserEntityByEmail(confirmationEntity.getUserEntity().getEmail());
        if (userEntity == null) { throw new ApiException("Incorrect token"); }
        UserValidation.verifyAccountStatus(userEntity);
        confirmationRepository.delete(confirmationEntity);
        return fromUserEntity(userEntity, userEntity.getRole(), getUserCredentialById(userEntity.getId()));
    }

    @Override
    public void updatePassword(String userId, String newPassword, String confirmNewPassword) {
        if (!confirmNewPassword.equals(newPassword)) { throw new ApiException("Passwords don't match. Please try again"); }
        var user = getUserByUserId(userId);
        var credentials = getUserCredentialById(user.getId());
        credentials.setPassword(encoder.encode(newPassword));
        credentialRepository.save(credentials);
    }

    @Override
    public void changePassword(String userId, String password, String newPassword, String confirmNewPassword) {
        if (!confirmNewPassword.equals(newPassword)) { throw new ApiException("Passwords don't match. Please try again"); }
        var userEntity = getUserEntityByUserId(userId);
        UserValidation.verifyAccountStatus(userEntity);
        var credentials = getUserCredentialById(userEntity.getId());
        if (!encoder.matches(password, credentials.getPassword())) { throw new ApiException("Existing password is incorrect. Please try again"); }
        credentials.setPassword(encoder.encode(newPassword));
        credentialRepository.save(credentials);
    }

    @Override
    public User updateUser(String userId, String firstName, String lastName, String email, String phone, String bio) {
        var userEntity = getUserEntityByUserId(userId);
        userEntity.setFirstName(firstName);
        userEntity.setLastName(lastName);
        userEntity.setEmail(email);
        userEntity.setPhone(phone);
        userEntity.setBio(bio);
        userRepository.save(userEntity);
        return fromUserEntity(userEntity, userEntity.getRole(), getUserCredentialById(userEntity.getId()));
    }

    @Override
    public void updateRole(String userId, String role) {
        var userEntity = getUserEntityByUserId(userId);
        userEntity.setRole(getRoleName(role));
        userRepository.save(userEntity);
    }

    @Override
    public void toggleAccountExpired(String userId) {
        var userEntity = getUserEntityByUserId(userId);
        userEntity.setAccountNonExpired(!userEntity.isAccountNonExpired());
        userRepository.save(userEntity);
    }

    @Override
    public void toggleAccountLocked(String userId) {
        var userEntity = getUserEntityByUserId(userId);
        userEntity.setAccountNonLocked(!userEntity.isAccountNonLocked());
        userRepository.save(userEntity);
    }

    @Override
    public void toggleAccountEnabled(String userId) {
        var userEntity = getUserEntityByUserId(userId);
        userEntity.setEnabled(!userEntity.isEnabled());
        userRepository.save(userEntity);
    }

    @Override
    public void toggleCredentialsExpired(String userId) {
        var userEntity = getUserEntityByUserId(userId);
        var credentials = getUserCredentialById(userEntity.getId());
        if (credentials.getUpdatedAt().plusDays(NINETY_DAYS).isAfter(LocalDateTime.now())) {
            credentials.setUpdatedAt(LocalDateTime.now());
        } else {
            credentials.setUpdatedAt(LocalDateTime.of(1995, 9, 7, 12, 0));
        }
        userRepository.save(userEntity);
    }

    @Override
    public String uploadPhoto(String userId, MultipartFile file) {
        var user = getUserEntityByUserId(userId);
        var photoUrl = photoFunction.apply(userId, file);
        user.setImageUrl(photoUrl + "?timestamp=" + System.currentTimeMillis());
        userRepository.save(user);
        return photoUrl;
    }

    private final BiFunction<String, MultipartFile, String> photoFunction = (id, file) -> {
        var filename = id + ".png";
        try {
            var fileStorageLocation = Paths.get(PHOTO_DIRECTORY).toAbsolutePath().normalize();
            if (!Files.exists(fileStorageLocation)) { Files.createDirectories(fileStorageLocation); }
            Files.copy(file.getInputStream(), fileStorageLocation.resolve(filename), REPLACE_EXISTING);

            return ServletUriComponentsBuilder
                    .fromCurrentContextPath()
                    .path("/user/image/" + filename).toUriString();
        } catch (Exception exception) { throw new ApiException("Unable to save image"); }
    };

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

    private ConfirmationEntity getUserConfirmation(UserEntity user) {
        return confirmationRepository.findByUserEntity(user).orElseThrow(null);
    }

    private UserEntity createNewUser(String firstName, String lastName, String email) {
        var role = getRoleName(Authority.USER.name());
        return createUserEntity(firstName, lastName, email, role);
    }
}

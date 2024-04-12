package com.adoustar.documentmanagement.utils;

import com.adoustar.documentmanagement.domain.dto.User;
import com.adoustar.documentmanagement.entity.CredentialEntity;
import com.adoustar.documentmanagement.entity.RoleEntity;
import com.adoustar.documentmanagement.entity.UserEntity;
import org.springframework.beans.BeanUtils;

import java.time.LocalDateTime;
import java.util.UUID;

import static com.adoustar.documentmanagement.constant.Constant.NINETY_DAYS;
import static org.apache.commons.lang3.StringUtils.EMPTY;


public class UserUtil {
    public static UserEntity createUserEntity(String firstName, String lastName, String email, RoleEntity role) {
        return UserEntity.builder()
                .userId(UUID.randomUUID().toString())
                .firstName(firstName)
                .lastName(lastName)
                .email(email)
                .lastLogin(LocalDateTime.now())
                .accountNonExpired(true)
                .accountNonLocked(true)
                .enabled(false)
                .isMfa(false)
                .loginAttempts(0)
                .qrCodeSecret(EMPTY)
                .phone(EMPTY)
                .bio(EMPTY)
                .imageUrl("https://cdn-icons-png.flaticon.com/512/149/149071.png")
                .role(role)
                .build();
    }

    public static User fromUserEntity(UserEntity userEntity, RoleEntity role, CredentialEntity userCredential) {
        User user = new User();
        BeanUtils.copyProperties(userEntity, user);
        user.setLastLogin(userEntity.getLastLogin().toString());
        user.setCredentialsNonExpired(isCredentialsNonExpired(userCredential));
        user.setCreatedAt(userEntity.getCreatedAt().toString());
        user.setUpdatedAt(userEntity.getUpdatedAt().toString());
        user.setRole(role.getName());
        user.setAuthorities(role.getAuthorities().getValue());
        return user;
    }

    private static boolean isCredentialsNonExpired(CredentialEntity credentialEntity) {
        return credentialEntity.getUpdatedAt().plusDays(NINETY_DAYS).isAfter(LocalDateTime.now());
    }
}

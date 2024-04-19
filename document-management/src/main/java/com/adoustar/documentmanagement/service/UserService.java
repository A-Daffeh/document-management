package com.adoustar.documentmanagement.service;

import com.adoustar.documentmanagement.domain.dto.User;
import com.adoustar.documentmanagement.entity.CredentialEntity;
import com.adoustar.documentmanagement.entity.RoleEntity;
import com.adoustar.documentmanagement.enums.LoginType;
import org.springframework.web.multipart.MultipartFile;

public interface UserService {
    void createUser(String firstname, String lastName, String email, String password);
    RoleEntity getRoleName(String name);

    void verifyAccountKey(String key);
    void updateLoginAttempt(String email, LoginType loginType);

    User getUserByUserId(String userId);

    User getUserByEmail(String email);

    CredentialEntity getUserCredentialById(Long id);

    User setUpMfa(Long userId);

    User cancelMfa(Long userId);

    User verifyQrCode(String userId, String qrCode);

    void resetPassword(String email);

    User verifyPasswordKey(String token);

    void updatePassword(String userId, String newPassword, String confirmNewPassword);

    void changePassword(String userId, String password, String newPassword, String confirmNewPassword);

    User updateUser(String userId, String firstName, String lastName, String email, String phone, String bio);

    void updateRole(String userId, String role);

    void toggleAccountExpired(String userId);

    void toggleAccountLocked(String userId);

    void toggleAccountEnabled(String userId);

    void toggleCredentialsExpired(String userId);


    String uploadPhoto(String userId, MultipartFile file);
}

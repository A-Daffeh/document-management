package com.adoustar.documentmanagement.service;

import com.adoustar.documentmanagement.domain.dto.User;
import com.adoustar.documentmanagement.entity.CredentialEntity;
import com.adoustar.documentmanagement.entity.RoleEntity;
import com.adoustar.documentmanagement.enums.LoginType;

public interface UserService {
    void createUser(String firstname, String lastName, String email, String password);
    RoleEntity getRoleName(String name);

    void verifyAccountKey(String key);
    void updateLoginAttempt(String email, LoginType loginType);

    User getUserByUserId(String userId);

    User getUserByEmail(String email);

    CredentialEntity getUserCredentialById(Long id);
}

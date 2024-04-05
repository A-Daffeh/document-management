package com.adoustar.documentmanagement.service;

import com.adoustar.documentmanagement.entity.RoleEntity;

public interface UserService {
    void createUser(String firstname, String lastName, String email, String password);
    RoleEntity getRoleName(String name);

    void verifyAccountKey(String key);
}

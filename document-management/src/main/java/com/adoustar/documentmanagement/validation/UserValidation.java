package com.adoustar.documentmanagement.validation;

import com.adoustar.documentmanagement.entity.UserEntity;
import com.adoustar.documentmanagement.exception.ApiException;

public class UserValidation {
    public static void verifyAccountStatus(UserEntity userEntity) {
        if (!userEntity.isEnabled()) { throw new ApiException("User is disabled"); }
        if (!userEntity.isAccountNonExpired()) { throw new ApiException("User is expired"); }
        if (!userEntity.isAccountNonLocked()) { throw new ApiException("User is locked"); }
    }
}

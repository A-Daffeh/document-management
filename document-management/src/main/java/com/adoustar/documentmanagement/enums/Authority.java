package com.adoustar.documentmanagement.enums;

import com.adoustar.documentmanagement.constant.Constant;

public enum Authority {
    MANAGER(Constant.MANAGER_AUTHORITIES),
    SUPER_ADMIN(Constant.SUPER_ADMIN_AUTHORITIES),
    ADMIN(Constant.ADMIN_AUTHORITIES),
    USER(Constant.USER_AUTHORITIES);

    private final String value;
    Authority(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}

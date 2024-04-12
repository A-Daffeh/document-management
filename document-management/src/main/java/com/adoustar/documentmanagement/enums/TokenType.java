package com.adoustar.documentmanagement.enums;

import com.adoustar.documentmanagement.domain.Token;

public enum TokenType {
    ACCESS("access-token"), REFERESH("refresh-token");

    private final  String value;

    TokenType(String value) {
        this.value = value;
    }

    public String getValue() {
        return this.value;
    }
}

package com.adoustar.documentmanagement.domain;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class Token {
    private String access;
    private String refreshToken;
}

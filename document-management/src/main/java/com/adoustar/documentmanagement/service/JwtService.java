package com.adoustar.documentmanagement.service;

import com.adoustar.documentmanagement.domain.Token;
import com.adoustar.documentmanagement.domain.TokenData;
import com.adoustar.documentmanagement.domain.dto.User;
import com.adoustar.documentmanagement.enums.TokenType;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.util.Optional;
import java.util.function.Function;

public interface JwtService {
    String createToken(User user, Function<Token, String> tokenFunction);
    Optional<String> extractToken(HttpServletRequest request, String tokenType);
    void addCookie(HttpServletResponse response, User user, TokenType tokenType);
    <T> T getTokenData(String token, Function<TokenData, T> tokenFunction);
    void removeCookie(HttpServletRequest request, HttpServletResponse response, String cookieName);
}

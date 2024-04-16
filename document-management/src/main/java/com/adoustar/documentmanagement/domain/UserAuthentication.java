package com.adoustar.documentmanagement.domain;

import com.adoustar.documentmanagement.domain.dto.User;
import com.adoustar.documentmanagement.exception.ApiException;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;

import java.util.Collection;

public class UserAuthentication extends AbstractAuthenticationToken {
    private static final String PASSWORD_PROTECTED = "[PASSWORD PROTECTED]";
    private static final String EMAIL_PROTECTED = "[EMAIL PROTECTED]";
    private User user;
    private final String email;
    private final String password;
    private final boolean authenticated;
    private UserAuthentication(String email, String password) {
        super(AuthorityUtils.NO_AUTHORITIES);
        this.email = email;
        this.password = password;
        this.authenticated = false;
    }

    private UserAuthentication(User user, Collection<? extends GrantedAuthority> authorities) {
        super(authorities);
        this.user = user;
        this.password = PASSWORD_PROTECTED;
        this.email = EMAIL_PROTECTED;
        this.authenticated = true;
    }

    public static UserAuthentication unauthenticated(String email, String password) {
        return new UserAuthentication(email, password);
    }

    public static UserAuthentication authenticated(User user, Collection<? extends GrantedAuthority> authorities) {
        return new UserAuthentication(user, authorities);
    }

    @Override
    public Object getCredentials() {
        return PASSWORD_PROTECTED;
    }

    @Override
    public Object getPrincipal() {
        return this.user;
    }

    @Override
    public void setAuthenticated(boolean authenticated) {
        throw new ApiException("You cannot set authentication");
    }

    @Override
    public boolean isAuthenticated() {
        return this.authenticated;
    }
    public String getPassword() {
        return this.password;
    }
    public String getEmail() {
        return this.email;
    }
}

package com.adoustar.documentmanagement.security;

import com.adoustar.documentmanagement.domain.UserAuthentication;
import com.adoustar.documentmanagement.domain.UserPrincipal;
import com.adoustar.documentmanagement.exception.ApiException;
import com.adoustar.documentmanagement.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.function.Consumer;
import java.util.function.Function;

import static com.adoustar.documentmanagement.constant.Constant.NINETY_DAYS;

@Component
@RequiredArgsConstructor
public class ApiAuthenticationProvider implements AuthenticationProvider {

    private final UserService userService;
    private final BCryptPasswordEncoder encoder;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        var userAuthentication = authenticationFunction.apply(authentication);
        var user = userService.getUserByEmail(userAuthentication.getEmail());
        if (user != null) {
            var userCredential = userService.getUserCredentialById(user.getId());
            if (userCredential.getUpdatedAt().minusDays(NINETY_DAYS).isAfter(LocalDateTime.now())) {
                throw new ApiException("Credentials Expired. Please reset your password.");
            }

            var userPrincipal = new UserPrincipal(user, userCredential);
            validAccount.accept(userPrincipal);
            if (encoder.matches(userAuthentication.getPassword(), userCredential.getPassword())) {
                return UserAuthentication.authenticated(user, userPrincipal.getAuthorities());
            } else {
                throw new BadCredentialsException("Email and/or Password incorrect. Please try again.");
            }
        } else throw  new ApiException("Unable to authenticate");
    }

    private final Function<Authentication, UserAuthentication> authenticationFunction = authentication ->
            (UserAuthentication) authentication;

    @Override
    public boolean supports(Class<?> authentication) {
        return UserAuthentication.class.isAssignableFrom(authentication);
    }

    private final Consumer<UserPrincipal> validAccount = userPrincipal -> {
        if (userPrincipal.isAccountNonLocked()) { throw new LockedException("Your is currently Locked."); }
        if (userPrincipal.isEnabled()) { throw new DisabledException("Your is currently Disabled."); }
        if (userPrincipal.isCredentialsNonExpired()) { throw new CredentialsExpiredException("Your password expired. Please update your password"); }
        if (userPrincipal.isAccountNonExpired()) { throw new DisabledException("Your account has expired. Please contact administrator."); }
    };
}

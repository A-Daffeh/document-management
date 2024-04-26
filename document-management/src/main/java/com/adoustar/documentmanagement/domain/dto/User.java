package com.adoustar.documentmanagement.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {
    private Long id;
    private Long createdBy;
    private Long updatedBy;
    private String userId;
    private String firstName;
    private String lastName;
    private String email;
    private String phone;
    private String bio;
    private String imageUrl;
    private String qrCodeImageUri;
    private String lastLogin;
    private String createdAt;
    private String updatedAt;
    private String role;
    private String authorities;
    private boolean accountNonExpired;
    private boolean accountNonLocked;
    private boolean credentialsNonExpired;
    private boolean enabled;
    private boolean mfa;
}

package com.adoustar.documentmanagement.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
@Entity
@Table(name = "users")
public class UserEntity extends Auditable {
    @Column(unique = true, updatable = false, nullable = false)
    private String userId;
    private String firstName;
    private String lastName;
    @Column(unique = true, nullable = false)
    private String email;
    private String phone;
    private String bio;
    private String imageUrl;
    private Integer loginAttempts;
    private LocalDateTime lastLogin;
    private boolean accountNonExpired;
    private boolean accountNonLocked;
    private boolean enabled;
    private boolean isMFA;
    @JsonIgnore
    private String qrCodeSecret;
    @Column(columnDefinition = "TEXT")
    private String qrCodeImageUri;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinTable(name = "user_roles",
            joinColumns = @JoinColumn(name = "user_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "role_id", referencedColumnName = "id"))
    private RoleEntity role;
}

package com.adoustar.documentmanagement.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_DEFAULT;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
@Entity
@Table(name = "users")
@JsonInclude(NON_DEFAULT)
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
    private boolean isMfa;
    @JsonIgnore
    private String qrCodeSecret;
    @Column(columnDefinition = "text")
    private String qrCodeImageUri;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinTable(name = "user_roles",
            joinColumns = @JoinColumn(name = "user_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "role_id", referencedColumnName = "id"))
    private RoleEntity role;
}

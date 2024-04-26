package com.adoustar.documentmanagement.service;

import com.adoustar.documentmanagement.entity.*;
import com.adoustar.documentmanagement.enums.*;
import com.adoustar.documentmanagement.exception.ApiException;
import com.adoustar.documentmanagement.repository.*;
import com.adoustar.documentmanagement.service.impl.UserServiceImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private CredentialRepository credentialRepository;
    @Mock
    private RoleRepository roleRepository;
    @InjectMocks
    private UserServiceImpl userServiceImpl;

    @Test
    @DisplayName("Test find user by ID")
    public void getUserByUserIdTest() {
        // Arrange
        var userEntity = new UserEntity();
        userEntity.setFirstName("Junior");
        userEntity.setId(1L);
        userEntity.setUserId("1");
        userEntity.setCreatedAt(LocalDateTime.of(1990, 11, 1, 1, 11, 11));
        userEntity.setUpdatedAt(LocalDateTime.of(1990, 11, 1, 1, 11, 11));
        userEntity.setLastLogin(LocalDateTime.of(1990, 11, 1, 1, 11, 11));

        var roleEntity = new RoleEntity("USER", Authority.USER);
        userEntity.setRole(roleEntity);

        var credentialEntity = new CredentialEntity();
        credentialEntity.setUpdatedAt(LocalDateTime.of(1990, 11, 1, 1, 11, 11));
        credentialEntity.setPassword("password");
        credentialEntity.setUserEntity(userEntity);

        when(userRepository.findUserByUserId("1")).thenReturn(Optional.of(userEntity));
        when(credentialRepository.getCredentialByUserEntityId(1L)).thenReturn(Optional.of(credentialEntity));

        // Act
        var userByUserId = userServiceImpl.getUserByUserId("1");

        // Assert
        assertThat(userByUserId.getFirstName()).isEqualTo("Junior");
        assertThat(userByUserId.getUserId()).isEqualTo("1");
        assertThat(userByUserId.getRole()).isEqualTo("USER");
    }


    @Test
    @DisplayName("Test update user role")
    public void updateRoleTest() {
        // Arrange
        var userEntity = new UserEntity();
        userEntity.setUserId("1");

        var roleEntity = new RoleEntity("ADMIN", Authority.ADMIN);

        when(userRepository.findUserByUserId("1")).thenReturn(Optional.of(userEntity));
        when(roleRepository.findByNameIgnoreCase("ADMIN")).thenReturn(Optional.of(roleEntity));

        // Act
        userServiceImpl.updateRole("1", "ADMIN");

        // Assert
        assertThat(userEntity.getRole().getName()).isEqualTo("ADMIN");
        verify(userRepository, times(1)).save(userEntity);
    }

    @Test
    @DisplayName("Test exception when user not found")
    public void userNotFoundExceptionTest() {
        // Arrange
        when(userRepository.findByEmailIgnoreCase("unknown@example.com")).thenReturn(Optional.empty());

        // Act & Assert
        var exception = assertThrows(ApiException.class, () -> userServiceImpl.getUserByEmail("unknown@example.com"));
        assertThat(exception.getMessage()).isEqualTo("User not found");
    }
}

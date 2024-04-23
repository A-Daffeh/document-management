package com.adoustar.documentmanagement.controller;

import com.adoustar.documentmanagement.domain.Response;
import com.adoustar.documentmanagement.domain.dto.User;
import com.adoustar.documentmanagement.domain.dtoRequest.*;
import com.adoustar.documentmanagement.enums.TokenType;
import com.adoustar.documentmanagement.handler.UserLogoutHandler;
import com.adoustar.documentmanagement.service.JwtService;
import com.adoustar.documentmanagement.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Map;

import static com.adoustar.documentmanagement.constant.Constant.FILE_STORAGE;
import static com.adoustar.documentmanagement.utils.RequestUtil.getResponse;
import static java.util.Collections.emptyMap;
import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.MediaType.IMAGE_JPEG_VALUE;
import static org.springframework.http.MediaType.IMAGE_PNG_VALUE;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    private final JwtService jwtService;
    private final UserLogoutHandler logoutHandler;

    @PostMapping("/register")
    public ResponseEntity<Response> addUser(@RequestBody @Valid UserRequest user, HttpServletRequest request) {
        userService.createUser(user.getFirstName(), user.getLastName(), user.getEmail(), user.getPassword());
        return ResponseEntity.created(URI.create("")).body(getResponse(request, emptyMap(), "Account created. Check  your email to enable your account", CREATED));
    }
    @GetMapping("/profile")
    public ResponseEntity<Response> profile(@AuthenticationPrincipal User userPrincipal, HttpServletRequest request) {
        var user = userService.getUserByUserId(userPrincipal.getUserId());
        return ResponseEntity.ok().body(getResponse(request, Map.of("user", user), "Profile retrieved", OK));
    }
    @PatchMapping("/update")
    public ResponseEntity<Response> update(@AuthenticationPrincipal User userPrincipal, @RequestBody UserRequest userRequest, HttpServletRequest request) {
        var user = userService.updateUser(userPrincipal.getUserId(), userRequest.getFirstName(), userRequest.getLastName(), userRequest.getEmail(), userRequest.getPhone(), userRequest.getBio());
        return ResponseEntity.ok().body(getResponse(request, Map.of("user", user), "User update successful", OK));
    }
    @PatchMapping("/changepassword")
    public ResponseEntity<Response> changePassword(@AuthenticationPrincipal User userPrincipal, @RequestBody UpdatePasswordRequest passwordRequest, HttpServletRequest request) {
        userService.changePassword(userPrincipal.getUserId(), passwordRequest.getPassword(), passwordRequest.getNewPassword(), passwordRequest.getConfirmNewPassword());
        return ResponseEntity.ok().body(getResponse(request, emptyMap(), "Change password successful", OK));
    }
    @PatchMapping("/updaterole")
    public ResponseEntity<Response> updateRole(@AuthenticationPrincipal User userPrincipal, @RequestBody RoleRequest roleRequest, HttpServletRequest request) {
        userService.updateRole(userPrincipal.getUserId(), roleRequest.getRole());
        return ResponseEntity.ok().body(getResponse(request, emptyMap(), "Role update successful", OK));
    }
    @PatchMapping("/toggleaccountexpired")
    public ResponseEntity<Response> toggleAccountExpired(@AuthenticationPrincipal User userPrincipal, HttpServletRequest request) {
        userService.toggleAccountExpired(userPrincipal.getUserId());
        return ResponseEntity.ok().body(getResponse(request, emptyMap(), "Account credentials updated successfully", OK));
    }
    @PatchMapping("/toggleaccountlocked")
    public ResponseEntity<Response> toggleAccountLocked(@AuthenticationPrincipal User userPrincipal, HttpServletRequest request) {
        userService.toggleAccountLocked(userPrincipal.getUserId());
        return ResponseEntity.ok().body(getResponse(request, emptyMap(), "Account credentials updated successfully", OK));
    }
    @PatchMapping("/toggleaccountenabled")
    public ResponseEntity<Response> toggleAccountEnabled(@AuthenticationPrincipal User userPrincipal, HttpServletRequest request) {
        userService.toggleAccountEnabled(userPrincipal.getUserId());
        return ResponseEntity.ok().body(getResponse(request, emptyMap(), "Account credentials updated successfully", OK));
    }
    @PatchMapping("/togglecredentialsexpired")
    public ResponseEntity<Response> toggleCredentialsExpired(@AuthenticationPrincipal User userPrincipal, HttpServletRequest request) {
        userService.toggleCredentialsExpired(userPrincipal.getUserId());
        return ResponseEntity.ok().body(getResponse(request, emptyMap(), "Account credentials updated successfully", OK));
    }
    @GetMapping("/verify/account")
    public ResponseEntity<Response> verifyAccount(@RequestParam("token") String key, HttpServletRequest request) {
        userService.verifyAccountKey(key);
        return ResponseEntity.ok().body(getResponse(request, emptyMap(), "Account verified", OK));
    }
    @PatchMapping("/mfa/setup")
    public ResponseEntity<Response> setUpMfa(@AuthenticationPrincipal User userPrincipal, HttpServletRequest request) {
        var user = userService.setUpMfa(userPrincipal.getId());
        return ResponseEntity.ok().body(getResponse(request, Map.of("user", user), "MFA setup successfully", OK));
    }
    @PatchMapping("/mfa/cancel")
    public ResponseEntity<Response> cancelMfa(@AuthenticationPrincipal User userPrincipal, HttpServletRequest request) {
        var user = userService.cancelMfa(userPrincipal.getId());
        return ResponseEntity.ok().body(getResponse(request, Map.of("user", user), "MFA canceled successfully", OK));
    }
    @PostMapping("/verify/qrcode")
    public ResponseEntity<Response> verifyQrCode(@RequestBody QrCodeRequest qrCodeRequest, HttpServletRequest request, HttpServletResponse response) {
        var user = userService.verifyQrCode(qrCodeRequest.getUserId(), qrCodeRequest.getQrCode());
        jwtService.addCookie(response, user, TokenType.ACCESS);
        jwtService.addCookie(response, user, TokenType.REFRESH);
        return ResponseEntity.ok().body(getResponse(request, Map.of("user", user), "QR Code Verified", OK));
    }
    @PostMapping("/resetpassword")
    public ResponseEntity<Response> resetPassword(@RequestBody @Valid EmailRequest emailRequest, HttpServletRequest request) {
        userService.resetPassword(emailRequest.getEmail());
        return ResponseEntity.ok().body(getResponse(request, emptyMap(), "We sent you an email to reset your password", OK));
    }
    @GetMapping("/verify/password")
    public ResponseEntity<Response> verifyPassword(@RequestParam("token") String token, HttpServletRequest request) {
        var user = userService.verifyPasswordKey(token);
        return ResponseEntity.ok().body(getResponse(request, Map.of("user", user), "Enter new password", OK));
    }
    @PostMapping("/resetpassword/reset")
    public ResponseEntity<Response> doResetPassword(@RequestBody @Valid ResetPasswordRequest resetPasswordRequest, HttpServletRequest request) {
        userService.updatePassword(resetPasswordRequest.getUserId(), resetPasswordRequest.getNewPassword(), resetPasswordRequest.getConfirmNewPassword());
        return ResponseEntity.ok().body(getResponse(request, emptyMap(), "Password reset successfully", OK));
    }
    @PatchMapping("/photo")
    public ResponseEntity<Response> uploadPhoto(@AuthenticationPrincipal User userPrincipal, @RequestParam("file") MultipartFile file, HttpServletRequest request) {
        var imageUrl = userService.uploadPhoto(userPrincipal.getUserId(), file);
        return ResponseEntity.ok().body(getResponse(request, Map.of("imageUrl", imageUrl), "Photo update successful", OK));
    }

    @GetMapping(value = "/image/{filename}", produces = { IMAGE_PNG_VALUE, IMAGE_JPEG_VALUE })
    public byte[] getPhoto(@PathVariable("filename") String filename) throws IOException {
        return Files.readAllBytes(Paths.get(FILE_STORAGE + filename));
    }

    @PostMapping("/logout")
    public ResponseEntity<Response> logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        logoutHandler.logout(request, response, authentication);
        return ResponseEntity.ok().body(getResponse(request, emptyMap(), "You've logged out successfully", OK));
    }
}

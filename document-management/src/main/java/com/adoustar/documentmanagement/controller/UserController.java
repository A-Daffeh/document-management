package com.adoustar.documentmanagement.controller;

import com.adoustar.documentmanagement.domain.Response;
import com.adoustar.documentmanagement.domain.dtoRequest.UserRequest;
import com.adoustar.documentmanagement.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

import static com.adoustar.documentmanagement.utils.RequestUtil.getResponse;
import static java.util.Collections.emptyMap;
import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.OK;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @PostMapping("/register")
    public ResponseEntity<Response> addUser(@RequestBody @Valid UserRequest user, HttpServletRequest request) {
        userService.createUser(user.getFirstName(), user.getLastName(), user.getEmail(), user.getPassword());
        return ResponseEntity.created(getUri()).body(getResponse(request, emptyMap(), "Account created. Check  your email to enable your account", CREATED));
    }

    @GetMapping("/verify/account")
    public ResponseEntity<Response> verifyAccount(@RequestParam("token") String key, HttpServletRequest request) {
        userService.verifyAccountKey(key);
        return ResponseEntity.ok().body(getResponse(request, emptyMap(), "Account verified", OK));
    }

    private URI getUri() {
        return URI.create("");
    }
}

package org.balaur.financemanagement.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.balaur.financemanagement.request.auth.UserForgotPasswordRequest;
import org.balaur.financemanagement.request.auth.UserLoginRequest;
import org.balaur.financemanagement.request.auth.UserRegisterRequest;
import org.balaur.financemanagement.request.auth.UserResetPasswordRequest;
import org.balaur.financemanagement.response.auth.AuthResponse;
import org.balaur.financemanagement.service.user.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    private final UserService userService;

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@RequestBody @Valid UserRegisterRequest request) {
        return userService.register(request);
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody @Valid UserLoginRequest request) {
        return userService.login(request);
    }

    @PostMapping("/forgot-password")
    public String forgotPassword(@RequestBody @Valid UserForgotPasswordRequest request) {
        return userService.forgotPassword(request);
    }

    @GetMapping("/reset-password/{token}")
    public boolean getResetPasswordToken(@PathVariable String token) {
        return userService.isResetPasswordTokenValid(token);
    }

    @PatchMapping("/reset-password")
    public ResponseEntity<AuthResponse> resetPassword(@RequestBody @Valid UserResetPasswordRequest request) {
        return userService.resetPassword(request);
    }
}

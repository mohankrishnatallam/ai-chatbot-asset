package com.cx.asset.controller;

import com.cx.asset.dto.AuthRequest;
import com.cx.asset.dto.AuthResponse;
import com.cx.asset.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/auth")
@Tag(name = "Auth", description = "User registration and login")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    @Operation(summary = "Register a new user")
    public ResponseEntity<?> register(@RequestBody AuthRequest request) {
        if (request == null) {
            return ResponseEntity.badRequest().body(errorBody("Request body is required"));
        }

        try {
            AuthResponse response = authService.register(request.getUsername(), request.getPassword());
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (IllegalArgumentException exception) {
            return ResponseEntity.badRequest().body(errorBody(exception.getMessage()));
        }
    }

    @PostMapping("/login")
    @Operation(summary = "Login and return user id")
    public ResponseEntity<?> login(@RequestBody AuthRequest request) {
        if (request == null) {
            return ResponseEntity.badRequest().body(errorBody("Request body is required"));
        }

        try {
            AuthResponse response = authService.login(request.getUsername(), request.getPassword());
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException exception) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorBody(exception.getMessage()));
        }
    }

    private Map<String, String> errorBody(String message) {
        return Map.of("message", message);
    }
}

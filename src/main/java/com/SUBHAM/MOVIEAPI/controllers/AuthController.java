package com.SUBHAM.MOVIEAPI.controllers;

import com.SUBHAM.MOVIEAPI.auth.entities.RefreshToken;
import com.SUBHAM.MOVIEAPI.auth.entities.User;
import com.SUBHAM.MOVIEAPI.auth.services.AuthService;
import com.SUBHAM.MOVIEAPI.auth.services.JwtService;
import com.SUBHAM.MOVIEAPI.auth.services.RefreshTokenService;
import com.SUBHAM.MOVIEAPI.auth.utils.AuthResponse;
import com.SUBHAM.MOVIEAPI.auth.utils.LoginRequest;
import com.SUBHAM.MOVIEAPI.auth.utils.RefreshTokenRequest;
import com.SUBHAM.MOVIEAPI.auth.utils.RegisterRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth/")
@CrossOrigin(origins = "*")
public class AuthController {

    private final AuthService authService;
    private final RefreshTokenService refreshTokenService;
    private final JwtService jwtService;

    public AuthController(AuthService authService, RefreshTokenService refreshTokenService, JwtService jwtService) {
        this.authService = authService;
        this.refreshTokenService = refreshTokenService;
        this.jwtService = jwtService;
    }

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@RequestBody RegisterRequest registerRequest) {
        return ResponseEntity.ok(authService.register(registerRequest));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody LoginRequest loginRequest) {
        return ResponseEntity.ok(authService.login(loginRequest));
    }

    @PostMapping("/refresh")
    public ResponseEntity<AuthResponse> refreshToken(@RequestBody RefreshTokenRequest refreshTokenRequest) {

        RefreshToken refreshToken = refreshTokenService.verifyRefreshToken(refreshTokenRequest.getRefreshToken());
        User user = refreshToken.getUser();

        String accessToken = jwtService.generateToken(user);

        return ResponseEntity.ok(AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken.getRefreshToken())
                .build());
    }
}

package com.kirsehirfilix.movieApi.controller;

import com.kirsehirfilix.movieApi.auth.entities.RefreshToken;
import com.kirsehirfilix.movieApi.auth.entities.User;
import com.kirsehirfilix.movieApi.auth.services.AuthService;
import com.kirsehirfilix.movieApi.auth.services.JwtService;
import com.kirsehirfilix.movieApi.auth.services.RefreshTokenService;
import com.kirsehirfilix.movieApi.auth.utils.AuthResponse;
import com.kirsehirfilix.movieApi.auth.utils.LoginRequest;
import com.kirsehirfilix.movieApi.auth.utils.RefreshTokenRequest;
import com.kirsehirfilix.movieApi.auth.utils.RegisterRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth/" )
public class AuthController {
    private final AuthService authService;
    private final RefreshTokenService refreshTokenService;
    private final JwtService jwtService;

    public  AuthController(AuthService authService, RefreshTokenService refreshTokenService, JwtService jwtService){
        this.authService =authService;
        this.refreshTokenService = refreshTokenService;
        this.jwtService = jwtService;
    }


    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@RequestBody RegisterRequest registerRequest){
        return ResponseEntity.ok(authService.register(registerRequest));
    }
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody LoginRequest loginRequest){
        return ResponseEntity.ok(authService.login(loginRequest));
    }
    @PostMapping("/refresh")
    public ResponseEntity<AuthResponse> refreshToken(@RequestBody RefreshTokenRequest refreshTokenRequest){
        RefreshToken refreshToken = refreshTokenService.verifyRefreshToken(refreshTokenRequest.getRefreshToken());
        User user = refreshToken.getUser();
        String acessToken = jwtService.generateToken(user);
        return  ResponseEntity.ok(AuthResponse.builder()
                .accessToken(acessToken)
                .refreshToken(refreshToken.getRefreshToken())
                .build());



    }
}

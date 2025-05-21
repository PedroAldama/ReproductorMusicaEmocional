package com.reproductor.music.controllers;

import com.reproductor.music.dto.response.DTOUserResponse;
import com.reproductor.music.dto.security.DTOLogin;
import com.reproductor.music.dto.security.DTOUserRegister;
import com.reproductor.music.dto.security.JwtAuthResponse;
import com.reproductor.music.services.auth.AuthService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@AllArgsConstructor
public class LoginController {
    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<JwtAuthResponse> login(@RequestBody DTOLogin loginRequest) {
        String token = authService.login(loginRequest);
        JwtAuthResponse response = JwtAuthResponse.builder()
                .accessToken(token)
                .build();
        return ResponseEntity.ok(response);
    }
    @PostMapping("/register")
    public ResponseEntity<DTOUserResponse> register(@RequestBody DTOUserRegister userRegister) {
        return ResponseEntity.status(HttpStatus.CREATED).body(authService.registerUser(userRegister));
    }
}

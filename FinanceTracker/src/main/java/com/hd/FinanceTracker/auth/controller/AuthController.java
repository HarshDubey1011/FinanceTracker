package com.hd.FinanceTracker.auth.controller;

import com.hd.FinanceTracker.common.dto.ApiResponse;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.hd.FinanceTracker.user.dto.*;
import com.hd.FinanceTracker.common.security.*;

@RestController
@RequestMapping("/api/v1/auth")
@AllArgsConstructor
public class AuthController {
    private final AuthService authService;
    private final JwtService jwtService;

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<AuthResponseDto>> register(@Valid @RequestBody RegisterRequestDto registerRequestDto) {
        AuthResponseDto dto = authService.register(registerRequestDto);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Registered Successfully!", dto));
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthResponseDto>> login(@Valid @RequestBody LoginRequestDto loginRequestDto) {
        AuthResponseDto dto = authService.login(loginRequestDto);
        return ResponseEntity.status(HttpStatus.OK)
                .body(ApiResponse.success("Login Successfully!", dto));
    }

    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<Void>> logout(@RequestHeader HttpHeaders headers) {
        String rawAccessToken = headers.getFirst("Authorization");
        String accessToken = rawAccessToken!=null && rawAccessToken.startsWith("Bearer ")
                ? rawAccessToken.substring(7) : null;

        String refreshToken = headers.getFirst("Refresh-Token");
        authService.logout(accessToken, refreshToken);
        return ResponseEntity.status(HttpStatus.OK)
                .body(ApiResponse.success("Logout Successfully!", null));
    }


    @PostMapping("/refresh")
    public ResponseEntity<ApiResponse<AuthResponseDto>> refresh(@RequestHeader("Refresh-Token") String refreshToken) {
        AuthResponseDto authResponseDto = authService.refresh(refreshToken);
        return ResponseEntity.status(HttpStatus.OK).body(
                ApiResponse.success("Refresh Successfully!", authResponseDto)
        );
    }
}

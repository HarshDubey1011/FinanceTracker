package com.hd.FinanceTracker.common.security;

import com.hd.FinanceTracker.common.exception.UserAlreadyExistsException;
import com.hd.FinanceTracker.common.exception.UserNotFoundException;
import com.hd.FinanceTracker.user.dto.*;
import com.hd.FinanceTracker.user.entity.*;
import com.hd.FinanceTracker.user.entity.*;
import com.hd.FinanceTracker.user.mappers.*;
import com.hd.FinanceTracker.user.repository.*;
import lombok.AllArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class AuthService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthResponseDto register(RegisterRequestDto registerRequestDto) {

        if (userRepository.findByEmail(registerRequestDto.email()).isPresent()) {
            throw new UserAlreadyExistsException("Email is already registered!");
        }

        User newUser = userMapper.toEntity(registerRequestDto);

        newUser.setPassword(passwordEncoder.encode(registerRequestDto.password()));

        // (Note: Setting Role.USER is great, but remember we also set this as a default in your Entity!)
        newUser.setRole(Role.USER);

        // 4. Save to PostgreSQL
        User savedUser = userRepository.save(newUser);

        String accessToken = jwtService.generateAccessToken(savedUser);
        String refreshToken = jwtService.generateRefreshToken(savedUser);

        return new AuthResponseDto(
                accessToken,
                refreshToken,
                savedUser.getEmail(),
                savedUser.getRole()
        );
    }

    public AuthResponseDto login(LoginRequestDto loginRequestDto) {
        var user = userRepository.findByEmail(loginRequestDto.email()).orElseThrow(() -> new RuntimeException("User not found!"));

        if(!passwordEncoder.matches(loginRequestDto.password(), user.getPassword())){
            throw new UserNotFoundException("Email or Password is incorrect!");
        }

        String accessToken = jwtService.generateAccessToken(user);
        String refreshToken = jwtService.generateRefreshToken(user);

        return new AuthResponseDto(
                accessToken,
                refreshToken,
                user.getEmail(),
                user.getRole()
        );

    }

    public User getCurrentUser() {
        var authenticatedUser = SecurityContextHolder.getContext().getAuthentication();
        var id = (String) authenticatedUser.getPrincipal();
        return userRepository.findById(Long.parseLong(id)).orElseThrow(() ->  new UserNotFoundException("User not found!"));
    }


}

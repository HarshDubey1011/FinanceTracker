package com.hd.FinanceTracker.user.dto;

import com.hd.FinanceTracker.user.entity.Role;

public record AuthResponseDto(
        String token,
        String refreshToken,
        String email,
        Role role
) {
}

package com.hd.FinanceTracker.category.dto;

import java.time.OffsetDateTime;

public record CategoryResponseDto(
        Long id,
        String categoryName,
        String categoryDescription,
        boolean isSystemGenerated,
        OffsetDateTime createdAt) {
}

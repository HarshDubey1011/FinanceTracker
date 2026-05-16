package com.hd.FinanceTracker.category.dto;

import jakarta.validation.constraints.NotBlank;

public record CategoryRequestDto(
        @NotBlank(message = "Category Name required")
        String categoryName,
        @NotBlank(message = "Category Description required")
        String categoryDescription
) {
}

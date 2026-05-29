package com.hd.FinanceTracker.budget.dto;

import com.hd.FinanceTracker.budget.entity.Period;
import com.hd.FinanceTracker.transaction.dto.CategorySummaryDto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;

public record BudgetResponseDto(
        Long id,
        BigDecimal amount,
        Period period,
        CategorySummaryDto category, // nullable — global budget if null
        LocalDate startDate,
        LocalDate endDate,
        OffsetDateTime createdAt
) {
}

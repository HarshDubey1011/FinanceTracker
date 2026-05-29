package com.hd.FinanceTracker.budget.dto;

import com.hd.FinanceTracker.budget.entity.Period;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;

public record BudgetRequestDto(
    @Positive
    @NotNull
    BigDecimal amount,
    @NotNull
    Period period,
    Long categoryId, // nullable — global budget if null
    @NotNull
    LocalDate startDate,
    @NotNull
    LocalDate endDate
) {

    @AssertTrue(message = "End Date must be After the Start Date")
    public boolean isEndDayAfterStartDate() {
        if(startDate == null || endDate == null) {
            return true;
        }
        return endDate.isAfter(startDate);
    }
}

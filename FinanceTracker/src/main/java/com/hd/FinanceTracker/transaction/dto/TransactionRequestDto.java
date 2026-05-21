package com.hd.FinanceTracker.transaction.dto;

import com.hd.FinanceTracker.transaction.entity.TransactionStatus;
import com.hd.FinanceTracker.transaction.entity.TransactionType;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

public record TransactionRequestDto(
        @NotNull(message = "Please enter the amount")
        @Positive(message = "Amount Should be greater than zero")
        BigDecimal amount,
        String transactionDescription,
        @NotNull(message = "Please provide transaction type")
        TransactionType transactionType,
        @NotNull(message = "Please provide a category id")
        Long categoryId,
        OffsetDateTime transactionDate
) {
}

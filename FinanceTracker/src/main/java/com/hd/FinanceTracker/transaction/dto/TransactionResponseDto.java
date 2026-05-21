package com.hd.FinanceTracker.transaction.dto;

import com.hd.FinanceTracker.transaction.entity.TransactionStatus;
import com.hd.FinanceTracker.transaction.entity.TransactionType;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

public record TransactionResponseDto(
        Long id,
        BigDecimal amount,
        String transactionDescription,
        CategorySummaryDto category,
        TransactionType transactionType,
        TransactionStatus status,
        OffsetDateTime transactionDate,
        OffsetDateTime createdAt
) {
}

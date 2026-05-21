package com.hd.FinanceTracker.transaction.entity;

import com.fasterxml.jackson.annotation.JsonCreator;

public enum TransactionStatus {
    PENDING,
    COMPLETED,
    REJECTED;

    @JsonCreator
    public static TransactionStatus fromValue(String value)
    {
        return TransactionStatus.valueOf(value.toUpperCase());
    }
}

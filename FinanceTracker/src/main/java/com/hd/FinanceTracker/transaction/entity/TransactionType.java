package com.hd.FinanceTracker.transaction.entity;

import com.fasterxml.jackson.annotation.JsonCreator;

public enum TransactionType {
    DEPOSIT,
    WITHDRAWAL,
    PURCHASE,
    REFUND;

    @JsonCreator
    public static TransactionType from(String value) {
        return TransactionType.valueOf(value.toUpperCase());
    }
}

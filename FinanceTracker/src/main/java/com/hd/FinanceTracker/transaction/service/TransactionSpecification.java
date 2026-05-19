package com.hd.FinanceTracker.transaction.service;

import com.hd.FinanceTracker.transaction.entity.Transaction;
import com.hd.FinanceTracker.transaction.entity.TransactionType;
import org.springframework.data.jpa.domain.Specification;

import java.time.OffsetDateTime;

public class TransactionSpecification {
    public static Specification<Transaction> byUserId(Long userId) {
        return (root,query,cb) -> cb.equal(root.get("user").get("id"), userId);
    }

    public static Specification<Transaction> byCategoryId(Long categoryId) {
        return (root, query, cb) ->  cb.equal(root.get("category").get("id"), categoryId);
    }

    public static Specification<Transaction> byTransactionType(TransactionType transactionType) {
        return (root, query, cb) -> cb.equal(root.get("transactionType"), transactionType);
    }

    public static Specification<Transaction> betweenDates(OffsetDateTime startDate, OffsetDateTime endDate) {
        return (root, query, cb) -> {
            if (startDate != null && endDate != null) {
                return cb.between(root.get("transactionDate"), startDate, endDate);
            } else if (startDate != null) {
                return cb.greaterThanOrEqualTo(root.get("transactionDate"), startDate);
            } else if (endDate != null) {
                return cb.lessThanOrEqualTo(root.get("transactionDate"), endDate);
            }
            return null; // No date filter applied
        };
    }
}

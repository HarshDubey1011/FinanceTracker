package com.hd.FinanceTracker.transaction.repository;

import com.hd.FinanceTracker.transaction.entity.Transaction;
import com.hd.FinanceTracker.transaction.entity.TransactionStatus;
import com.hd.FinanceTracker.transaction.entity.TransactionType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

public interface TransactionRepository extends JpaRepository<Transaction, Long>, JpaSpecificationExecutor<Transaction> {

    @Query("SELECT t FROM Transaction t where t.user.id=:userId AND t.transactionType=:transactionType")
    Page<Transaction> findByUserIdAndTransactionType(Long userId, TransactionType transactionType, Pageable pageable);

    Page<Transaction> findByUserIdAndTransactionDateBetween(Long userId, OffsetDateTime startDate, OffsetDateTime endDate, Pageable pageable);

    @Query("SELECT t from Transaction t where t.user.id=:userId AND t.status=:transactionStatus")
    Page<Transaction> findByUserIdAndTransactionStatus(Long userId, TransactionStatus transactionStatus, Pageable pageable);

    // All transactions for user — base query
    Page<Transaction> findByUserId(Long userId, Pageable pageable);

    // For budget calculation — sum spending in category within date range
    @Query("SELECT COALESCE(SUM(t.amount), 0) FROM Transaction t " +
            "WHERE t.user.id = :userId " +
            "AND t.category.id = :categoryId " +
            "AND t.transactionDate BETWEEN :start AND :end " +
            "AND t.status =:transactionStatus")
    BigDecimal sumSpendingByCategoryAndPeriod(Long userId, Long categoryId,
                                              OffsetDateTime start, OffsetDateTime end, TransactionStatus transactionStatus);
}

// We added JpaSpecificationExecutor to do dynamic filtering in service layer instead of writing 8 separate repository methods for every combination you write one Specification
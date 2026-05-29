package com.hd.FinanceTracker.transaction.repository;

import com.hd.FinanceTracker.transaction.entity.Transaction;
import com.hd.FinanceTracker.transaction.entity.TransactionStatus;
import com.hd.FinanceTracker.transaction.entity.TransactionType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.List;

public interface TransactionRepository extends JpaRepository<Transaction, Long>, JpaSpecificationExecutor<Transaction> {
    // For budget calculation — sum spending in category within date range
    @Query("SELECT COALESCE(SUM(t.amount), 0) FROM Transaction t " +
            "WHERE t.user.id = :userId " +
            "AND t.category.id = :categoryId " +
            "AND t.transactionDate >= :start AND t.transactionDate <= :end " +
            "AND t.status = :transactionStatus " +
            "AND t.transactionType IN :types")
    BigDecimal sumSpendingByCategoryAndPeriod(
            @Param("userId") Long userId,
            @Param("categoryId") Long categoryId,
            @Param("start") OffsetDateTime start, // Changed back to OffsetDateTime
            @Param("end") OffsetDateTime end,     // Changed back to OffsetDateTime
            @Param("transactionStatus") TransactionStatus transactionStatus,
            @Param("types") List<TransactionType> types
    );

    @Query("SELECT COALESCE(SUM(t.amount), 0) FROM Transaction t " +
            "WHERE t.user.id = :userId " +
            "AND t.transactionDate BETWEEN :start AND :end " +
            "AND t.status = :transactionStatus "+
            "AND t.transactionType IN :types")
    BigDecimal sumAllSpendingForPeriod(
            @Param("userId") Long userId,
            @Param("start") LocalDate start,
            @Param("end") LocalDate end,
            @Param("transactionStatus") TransactionStatus transactionStatus,
            @Param("types") List<TransactionType> transactionType
    );
}


// We added JpaSpecificationExecutor to do dynamic filtering in service layer instead of writing 8 separate repository methods for every combination you write one Specification
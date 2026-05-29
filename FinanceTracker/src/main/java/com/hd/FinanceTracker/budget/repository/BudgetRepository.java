package com.hd.FinanceTracker.budget.repository;

import com.hd.FinanceTracker.budget.entity.Budget;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.List;

public interface BudgetRepository extends JpaRepository<Budget, Long>, JpaSpecificationExecutor<Budget> {

    // 1. Overlap Check for Specific Category Budgets
    @Query("SELECT CASE WHEN COUNT(b) > 0 THEN true ELSE false END FROM Budget b " +
            "WHERE b.user.id = :userId " +
            "AND b.category.id = :categoryId " +
            "AND (b.startDate <= :endDate AND b.endDate >= :startDate)")
    boolean existsByUserIdAndCategoryIdAndDateOverlap(
            @Param("userId") Long userId,
            @Param("categoryId") Long categoryId,
            @Param("startDate")LocalDate startDate,
            @Param("endDate") LocalDate endDate
            );

    // 2. Overlap Check for Global Budgets (Category is NULL)
    @Query("SELECT CASE WHEN COUNT(b) > 0 THEN true ELSE false END FROM Budget b " +
            "WHERE b.user.id = :userId " +
            "AND b.category IS NULL " +
            "AND (b.startDate <= :endDate AND b.endDate >= :startDate)")
    boolean existsGlobalBudgetByUserIdAndDateOverlap(
            @Param("userId") Long userId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate
    );

    // 3. For the Budget Overspend Alert System
    // Finds the specific budget that covers the date of a newly inserted transaction
    @Query("SELECT b FROM Budget b " +
            "WHERE b.user.id = :userId " +
            "AND (b.category.id = :categoryId OR b.category IS NULL) " +
            "AND CAST(:transactionDate AS DATE) BETWEEN b.startDate AND b.endDate " +
            "ORDER BY b.category.id ASC") // Prioritizes Specific Category Budgets over Global ones
    List<Budget> findActiveBudgetsForTransaction(
            @Param("userId") Long userId,
            @Param("categoryId") Long categoryId,
            @Param("transactionDate") OffsetDateTime transactionDate
    );

    // Updated Specific Overlap Query
    @Query("SELECT CASE WHEN COUNT(b) > 0 THEN true ELSE false END FROM Budget b " +
            "WHERE b.user.id = :userId " +
            "AND b.id != :excludeBudgetId " + // <--- ADD THIS
            "AND b.category.id = :categoryId " +
            "AND (b.startDate <= :endDate AND b.endDate >= :startDate)")
    boolean existsByUserIdAndCategoryIdAndDateOverlapExcludingId(
            @Param("userId") Long userId,
            @Param("categoryId") Long categoryId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate,
            @Param("excludeBudgetId") Long excludeBudgetId // <--- ADD THIS
    );

    // Updated Global Overlap Query
    @Query("SELECT CASE WHEN COUNT(b) > 0 THEN true ELSE false END FROM Budget b " +
            "WHERE b.user.id = :userId " +
            "AND b.id != :excludeBudgetId " +
            "AND b.category IS NULL " +
            "AND (b.startDate <= :endDate AND b.endDate >= :startDate)")
    boolean existsGlobalBudgetByUserIdAndDateOverlapExcludingId(
            @Param("userId") Long userId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate,
            @Param("excludeBudgetId") Long excludeBudgetId
    );
}

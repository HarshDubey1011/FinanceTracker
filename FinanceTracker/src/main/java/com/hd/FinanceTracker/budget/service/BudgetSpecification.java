package com.hd.FinanceTracker.budget.service;

import com.hd.FinanceTracker.budget.entity.Budget;
import com.hd.FinanceTracker.budget.entity.Period;
import org.springframework.data.jpa.domain.Specification;
import java.time.LocalDate;

public class BudgetSpecification {

    public static Specification<Budget> byUserId(Long userId) {
        return (root, query, cb) -> cb.equal(root.get("user").get("id"), userId);
    }

    public static Specification<Budget> byCategoryId(Long categoryId) {
        return (root, query, cb) -> categoryId == null ? null : cb.equal(root.get("category").get("id"), categoryId);
    }

    public static Specification<Budget> byPeriod(Period period) {
        return (root, query, cb) -> period == null ? null : cb.equal(root.get("period"), period);
    }

    public static Specification<Budget> betweenDates(LocalDate startDate, LocalDate endDate) {
        return (root, query, cb) -> {
            if(startDate == null && endDate == null) return null; // ← no filter if both null

            return cb.or(
                    cb.between(root.get("startDate"), startDate, endDate),
                    cb.between(root.get("endDate"), startDate, endDate),
                    cb.and(
                            cb.lessThanOrEqualTo(root.get("startDate"), startDate),
                            cb.greaterThanOrEqualTo(root.get("endDate"), endDate)
                    )
            );
        };
    }
}

package com.hd.FinanceTracker.budget.service;

import com.hd.FinanceTracker.budget.dto.BudgetRequestDto;
import com.hd.FinanceTracker.budget.dto.BudgetResponseDto;
import com.hd.FinanceTracker.budget.entity.Budget;
import com.hd.FinanceTracker.budget.entity.Period;
import com.hd.FinanceTracker.budget.mapper.BudgetMapper;
import com.hd.FinanceTracker.budget.repository.BudgetRepository;
import com.hd.FinanceTracker.category.repository.CategoryRepository;
import com.hd.FinanceTracker.common.exception.AccessDeniedException;
import com.hd.FinanceTracker.common.exception.DuplicateResourceException;
import com.hd.FinanceTracker.common.exception.ResourceNotFoundException;
import com.hd.FinanceTracker.common.exception.UserNotFoundException;
import com.hd.FinanceTracker.common.security.AuthService;
import com.hd.FinanceTracker.common.service.EmailService;
import com.hd.FinanceTracker.transaction.entity.TransactionStatus;
import com.hd.FinanceTracker.transaction.entity.TransactionType;
import com.hd.FinanceTracker.transaction.repository.TransactionRepository;
import com.hd.FinanceTracker.user.repository.UserRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Objects;

@Slf4j
@Service
@AllArgsConstructor
public class BudgetService {
    private final BudgetRepository budgetRepository;
    private final AuthService authService;
    private final CategoryRepository categoryRepository;
    private final BudgetMapper budgetMapper;
    private final TransactionRepository transactionRepository;
    private final UserRepository userRepository;
    private static final List<TransactionType> spendingTypes = List.of(TransactionType.WITHDRAWAL, TransactionType.PURCHASE);
    private final EmailService emailService;

    public BudgetResponseDto createBudget(BudgetRequestDto request) {
        var user = authService.getCurrentUser();

        // Handle category budget
        Budget budget = budgetMapper.fromDto(request);
        budget.setUser(user);

        if(request.categoryId() != null) {
            var category = categoryRepository.findById(request.categoryId())
                    .orElseThrow(() -> new ResourceNotFoundException("Category not found"));

            if(!category.getIsSystemGenerated() && !category.getUser().getId().equals(user.getId())) {
                throw new AccessDeniedException("You don't have access to this category");
            }

            if(budgetRepository.existsByUserIdAndCategoryIdAndDateOverlap(
                    user.getId(), request.categoryId(), request.startDate(), request.endDate())) {
                throw new DuplicateResourceException("Budget already exists for this category and period");
            }

            budget.setCategory(category);

        } else {
            // Global budget
            if(budgetRepository.existsGlobalBudgetByUserIdAndDateOverlap(
                    user.getId(), request.startDate(), request.endDate())) {
                throw new DuplicateResourceException("Global budget already exists for this period");
            }
            budget.setCategory(null);
        }

        return budgetMapper.toDto(budgetRepository.save(budget));
    }

    public Page<BudgetResponseDto> getBudget(int page, int size, Long categoryId, Period period, LocalDate startDate, LocalDate endDate) {
        size = Math.min(50, size);
        var user =  authService.getCurrentUser();

        Pageable pageable = PageRequest.of(page, size, Sort.by("startDate").descending());

        Specification<Budget> specification = Specification
                .where(BudgetSpecification.byUserId(user.getId()))
                .and(BudgetSpecification.byCategoryId(categoryId))
                .and(BudgetSpecification.byPeriod(period))
                .and(BudgetSpecification.betweenDates(startDate, endDate));

        Page<Budget> budgets = budgetRepository.findAll(specification, pageable);
        return budgets.map(budgetMapper::toDto);
    }

    public BudgetResponseDto updateBudget(Long id, BudgetRequestDto request) {
        var budget = budgetRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Budget not found"));
        var user = authService.getCurrentUser();

        if(!budget.getUser().getId().equals(user.getId())) {
            throw new AccessDeniedException("Access Denied");
        }

        if(request.categoryId() != null) {
            var category = categoryRepository.findById(request.categoryId())
                    .orElseThrow(() -> new ResourceNotFoundException("Category not found"));

            if(!category.getIsSystemGenerated() && !category.getUser().getId().equals(user.getId())) {
                throw new AccessDeniedException("You don't have access to this category");
            }

            // FIXED: Passing budget.getId() to exclude the current budget from the overlap check
            if(budgetRepository.existsByUserIdAndCategoryIdAndDateOverlapExcludingId(
                    user.getId(), request.categoryId(), request.startDate(), request.endDate(), budget.getId())) {
                throw new DuplicateResourceException("Budget already exists for this category and period");
            }
            budget.setCategory(category);
        } else {
            if(budgetRepository.existsGlobalBudgetByUserIdAndDateOverlapExcludingId(
                    user.getId(), request.startDate(), request.endDate(), budget.getId())) {
                throw new DuplicateResourceException("Global budget already exists for this period");
            }
            budget.setCategory(null);
        }
        if(!Objects.equals(request.amount(), budget.getAmount())) {
            budget.setAlertSent(false);
        }
        budgetMapper.updateBudgetFromDto(request, budget);
        return budgetMapper.toDto(budgetRepository.save(budget));
    }

    public void deleteBudget(Long id) {
        var user =  authService.getCurrentUser();
        var budget =  budgetRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Budget not found"));

        if(!budget.getUser().getId().equals(user.getId())) {
            throw new AccessDeniedException("Access Denied");
        }

        budget.setDeletedAt(OffsetDateTime.now());
        budgetRepository.save(budget);
    }

    public void checkBudgetAlert(Long userId, Long categoryId, OffsetDateTime transactionDate) {

        List<Budget> budgetList = budgetRepository
                .findActiveBudgetsForTransaction(userId, categoryId, transactionDate);

        for(var budget : budgetList) {
            if(budget.isAlertSent()) continue;
            BigDecimal spent;

            OffsetDateTime startRange = budget.getStartDate().atStartOfDay().atOffset(java.time.ZoneOffset.UTC);
            OffsetDateTime endRange = budget.getEndDate().atTime(23, 59, 59).atOffset(java.time.ZoneOffset.UTC);

            if(budget.getCategory() != null) {
                // Specific category budget
                spent = transactionRepository.sumSpendingByCategoryAndPeriod(
                        userId,
                        budget.getCategory().getId(),
                        startRange,
                        endRange,
                        TransactionStatus.COMPLETED,
                        spendingTypes
                );
            } else {
                // Global budget — sum ALL spending
                spent = transactionRepository.sumAllSpendingForPeriod(
                        userId,
                        budget.getStartDate(),
                        budget.getEndDate(),
                        TransactionStatus.COMPLETED,
                        spendingTypes
                );
            }

            if(spent.compareTo(budget.getAmount()) >= 0) {
                var user = userRepository.findById(userId)
                        .orElseThrow(() -> new UserNotFoundException("User not found"));
                emailService.sendBudgetAlert(user.getName(), user.getEmail(), budget, spent);
                budget.setAlertSent(true);
                budgetRepository.save(budget);
                log.warn("Budget alert triggered for userId: {}, budgetId: {}, spent: {}, limit: {}",
                        userId, budget.getId(), spent, budget.getAmount());
            }
        }
    }
}

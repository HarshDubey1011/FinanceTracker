package com.hd.FinanceTracker.budget.controller;

import com.hd.FinanceTracker.budget.dto.BudgetRequestDto;
import com.hd.FinanceTracker.budget.dto.BudgetResponseDto;
import com.hd.FinanceTracker.budget.entity.Period;
import com.hd.FinanceTracker.budget.repository.BudgetRepository;
import com.hd.FinanceTracker.budget.service.BudgetService;
import com.hd.FinanceTracker.common.dto.ApiResponse;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.OffsetDateTime;

@RestController
@RequestMapping("/api/v1/budgets")
@AllArgsConstructor
public class BudgetController {
    private final BudgetService budgetService;

    @PostMapping
    public ResponseEntity<ApiResponse<BudgetResponseDto>> createBudget(@Valid @RequestBody BudgetRequestDto budgetRequestDto) {
        var budget = budgetService.createBudget(budgetRequestDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(
                ApiResponse.success("Budget created successfully", budget)
        );
    }

    @GetMapping
    public ResponseEntity<ApiResponse<Page<BudgetResponseDto>>> getAllBudgets(@RequestParam(defaultValue = "0") int page,
                                                                              @RequestParam(defaultValue = "10") int size,
                                                                              @RequestParam(required = false) Long categoryId,
                                                                              @RequestParam(required = false) Period period,
                                                                              @RequestParam(required = false) LocalDate startDate,
                                                                              @RequestParam(required = false) LocalDate endDate) {
        var budget = budgetService.getBudget(page, size, categoryId, period, startDate, endDate);
        return ResponseEntity.status(HttpStatus.OK).body(
          ApiResponse.success("Budgets found successfully", budget)
        );
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<BudgetResponseDto>> updateBudget(@PathVariable Long id, @Valid @RequestBody BudgetRequestDto budgetRequestDto) {
        var budget = budgetService.updateBudget(id,  budgetRequestDto);
        return  ResponseEntity.status(HttpStatus.OK).body(
          ApiResponse.success("Budget updated successfully", budget)
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteBudget(@PathVariable Long id) {
        budgetService.deleteBudget(id);
        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success("Budget deleted successfully", null));
    }
}

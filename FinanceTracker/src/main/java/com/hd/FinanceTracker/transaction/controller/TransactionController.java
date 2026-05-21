package com.hd.FinanceTracker.transaction.controller;

import com.hd.FinanceTracker.common.dto.ApiResponse;
import com.hd.FinanceTracker.transaction.dto.TransactionRequestDto;
import com.hd.FinanceTracker.transaction.dto.TransactionResponseDto;
import com.hd.FinanceTracker.transaction.entity.TransactionType;
import com.hd.FinanceTracker.transaction.service.TransactionService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.OffsetDateTime;

@RestController
@RequestMapping("/api/v1/transactions")
@AllArgsConstructor
public class TransactionController {
    private final TransactionService transactionService;

    @PostMapping
    public ResponseEntity<ApiResponse<TransactionResponseDto>> createTransaction(@Valid @RequestBody TransactionRequestDto transactionRequestDto) {
        TransactionResponseDto transaction = transactionService.createTransaction(transactionRequestDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success("Transaction created successfully", transaction));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<Page<TransactionResponseDto>>> getTransaction(@RequestParam(defaultValue="0") int page, @RequestParam(defaultValue="10") int size, @RequestParam(required = false) Long categoryId,
                                                                              @RequestParam(required = false) TransactionType transactionType,
                                                                              @RequestParam(required = false) OffsetDateTime startDate,
                                                                              @RequestParam(required = false) OffsetDateTime endDate) {
        var pageTransactionDto = transactionService.getTransactions(page, size, categoryId, transactionType, startDate, endDate);
        return  ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success("Transactions found", pageTransactionDto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<TransactionResponseDto>> updateTransaction(@PathVariable Long id, @Valid @RequestBody TransactionRequestDto transactionRequestDto) {
        TransactionResponseDto transaction = transactionService.updateTransaction(id, transactionRequestDto);
        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success("Transaction updated successfully", transaction));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteTransaction(@PathVariable Long id) {
        transactionService.deleteTransaction(id);
        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success("Transaction Deleted Successfully", null));
    }
}

package com.hd.FinanceTracker.transaction.service;

import com.hd.FinanceTracker.category.repository.CategoryRepository;
import com.hd.FinanceTracker.common.exception.AccessDeniedException;
import com.hd.FinanceTracker.common.exception.ResourceNotFoundException;
import com.hd.FinanceTracker.common.security.AuthService;
import com.hd.FinanceTracker.transaction.dto.TransactionRequestDto;
import com.hd.FinanceTracker.transaction.dto.TransactionResponseDto;
import com.hd.FinanceTracker.transaction.entity.Transaction;
import com.hd.FinanceTracker.transaction.entity.TransactionStatus;
import com.hd.FinanceTracker.transaction.entity.TransactionType;
import com.hd.FinanceTracker.transaction.mappers.TransactionMapper;
import com.hd.FinanceTracker.transaction.repository.TransactionRepository;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;

@Service
@AllArgsConstructor
public class TransactionService {
    private final CategoryRepository categoryRepository;
    private final TransactionRepository transactionRepository;
    private final AuthService authService;
    private final TransactionMapper transactionMapper;

    public TransactionResponseDto createTransaction(TransactionRequestDto requestDto) {
        var category = categoryRepository.findById(requestDto.categoryId()).orElseThrow(() -> new ResourceNotFoundException("Category not found"));
        var user = authService.getCurrentUser();

        if(!category.getIsSystemGenerated() && !category.getUser().getId().equals(user.getId())) {
            throw new AccessDeniedException("You don't have access to this category");
        }

        var transaction = transactionMapper.fromDto(requestDto);
        transaction.setCategory(category);
        transaction.setUser(user);
        transaction.setStatus(TransactionStatus.PENDING);
        transaction.setTransactionDate(requestDto.transactionDate()!=null ? requestDto.transactionDate(): OffsetDateTime.now());

        var savedTransaction = transactionRepository.save(transaction);
        return transactionMapper.toDto(savedTransaction);

        // Todo async budget overspend alert
    }

    public TransactionResponseDto updateTransaction(Long transactionId, TransactionRequestDto requestDto) {
       var user = authService.getCurrentUser();
       var transaction = transactionRepository.findById(transactionId).orElseThrow(() -> new ResourceNotFoundException("Transaction not found"));

       // Ownership check
        if(!transaction.getUser().getId().equals(user.getId())) {
            throw new AccessDeniedException("Access Denied");
        }

        if(!transaction.getCategory().getId().equals(requestDto.categoryId())) {
            var category = categoryRepository.findById(requestDto.categoryId()).orElseThrow(() -> new ResourceNotFoundException("Category not found"));
            if(!category.getIsSystemGenerated() && !category.getUser().getId().equals(user.getId())) {
                throw new AccessDeniedException("You don't have access to this category");
            }
            transaction.setCategory(category);
        }

        transactionMapper.updateTransactionFromDto(requestDto, transaction);
        return transactionMapper.toDto(transactionRepository.save(transaction));
    }

    public void deleteTransaction(Long transactionId) {
        var user = authService.getCurrentUser();
        var transaction = transactionRepository.findById(transactionId)
                .orElseThrow(() -> new ResourceNotFoundException("Transaction not found"));

        if(!transaction.getUser().getId().equals(user.getId())) {
            throw new AccessDeniedException("Access Denied");
        }

        transaction.setDeletedAt(OffsetDateTime.now());
        transactionRepository.save(transaction);
    }

    public Page<TransactionResponseDto> getTransactions(int page, int size,
                                                        Long categoryId, TransactionType type,
                                                        OffsetDateTime startDate,
                                                        OffsetDateTime endDate) {
        var user =  authService.getCurrentUser();
        Pageable pageable = PageRequest.of(page, size, Sort.by("transactionDate").descending());

        // Chain the dynamic filters together
        Specification<Transaction> spec = Specification
                .where(TransactionSpecification.byUserId(user.getId()))
                .and(TransactionSpecification.byCategoryId(categoryId))
                .and(TransactionSpecification.byTransactionType(type))
                .and(TransactionSpecification.betweenDates(startDate, endDate));

        Page<Transaction> transactions = transactionRepository.findAll(spec, pageable);
        return transactions.map(transactionMapper::toDto);

    }
}

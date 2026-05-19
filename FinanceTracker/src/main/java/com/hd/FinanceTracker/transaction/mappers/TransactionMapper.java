package com.hd.FinanceTracker.transaction.mappers;

import com.hd.FinanceTracker.transaction.dto.TransactionRequestDto;
import com.hd.FinanceTracker.transaction.dto.TransactionResponseDto;
import com.hd.FinanceTracker.transaction.entity.Transaction;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

@Mapper(componentModel = "spring")
public interface TransactionMapper {

    @Mapping(target = "category.id", source = "category.id")
    @Mapping(target = "category.categoryName", source = "category.categoryName")
    TransactionResponseDto toDto(Transaction transaction);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "category", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "deletedAt", ignore = true)
    Transaction fromDto(TransactionRequestDto transactionRequestDto);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "category", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "deletedAt", ignore = true)
    void updateTransactionFromDto(TransactionRequestDto requestDto, @MappingTarget Transaction transaction);

}

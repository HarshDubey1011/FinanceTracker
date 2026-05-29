package com.hd.FinanceTracker.budget.mapper;


import com.hd.FinanceTracker.budget.dto.BudgetRequestDto;
import com.hd.FinanceTracker.budget.dto.BudgetResponseDto;
import com.hd.FinanceTracker.budget.entity.Budget;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface BudgetMapper {
    @Mapping(target = "category.id", source = "category.id")
    @Mapping(target = "category.categoryName", source = "category.categoryName")
    BudgetResponseDto toDto(Budget budget);

    @Mapping(target = "id", ignore =  true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "category", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "deletedAt", ignore = true)
    Budget fromDto(BudgetRequestDto budgetRequestDto);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "category", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "deletedAt", ignore = true)
    void updateBudgetFromDto(BudgetRequestDto budgetRequestDto, @MappingTarget Budget budget);
}

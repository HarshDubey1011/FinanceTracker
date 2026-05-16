package com.hd.FinanceTracker.category.mappers;

import com.hd.FinanceTracker.category.dto.CategoryRequestDto;
import com.hd.FinanceTracker.category.dto.CategoryResponseDto;
import com.hd.FinanceTracker.category.entity.Category;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface CategoryMapper {
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "isSystemGenerated", ignore = true)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "deletedAt", ignore = true)
    Category toCategory(CategoryRequestDto categoryRequestDto);


    CategoryResponseDto toCategoryResponseDto(Category category);

    // NEW: Automatically update an existing entity from a DTO!
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "isSystemGenerated", ignore = true)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "deletedAt", ignore = true)
    void updateCategoryFromDto(CategoryRequestDto categoryRequestDto, @MappingTarget Category category);
}

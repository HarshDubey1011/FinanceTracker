package com.hd.FinanceTracker.category.service;

import com.hd.FinanceTracker.category.dto.CategoryRequestDto;
import com.hd.FinanceTracker.category.dto.CategoryResponseDto;
import com.hd.FinanceTracker.category.entity.Category;
import com.hd.FinanceTracker.category.mappers.CategoryMapper;
import com.hd.FinanceTracker.category.repository.CategoryRepository;
import com.hd.FinanceTracker.common.exception.AccessDeniedException;
import com.hd.FinanceTracker.common.exception.DuplicateResourceException;
import com.hd.FinanceTracker.common.exception.ResourceNotFoundException;
import com.hd.FinanceTracker.common.security.AuthService;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.List;

@Service
@AllArgsConstructor
public class CategoryService {
    private final CategoryRepository categoryRepository;
    private final AuthService authService;
    private final CategoryMapper categoryMapper;

    public CategoryResponseDto createCategory(CategoryRequestDto categoryRequestDto) {
        var user = authService.getCurrentUser();
        if(categoryRepository.existsByUserIdAndCategoryName(user.getId(), categoryRequestDto.categoryName())) {
            throw new DuplicateResourceException("Category already exists for the user");
        }

        var category = categoryMapper.toCategory(categoryRequestDto);
        category.setUser(user);
        category.setIsSystemGenerated(false);
        var saved = categoryRepository.save(category);
        return categoryMapper.toCategoryResponseDto(saved);
    }

    public Page<CategoryResponseDto> getAllCategories(int page, int size) {
        var user = authService.getCurrentUser();
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());

        Page<Category> categoryPage = categoryRepository.findAllVisibleToUser(user.getId(), pageable);
        return categoryPage.map(categoryMapper::toCategoryResponseDto);
    }

    public CategoryResponseDto updateCategory(CategoryRequestDto categoryRequestDto, Long categoryId) {
        var category = categoryRepository.findById(categoryId).orElseThrow(() -> new ResourceNotFoundException("Category not found"));

        var user = authService.getCurrentUser();

        if(category.getIsSystemGenerated()) {
            throw new AccessDeniedException("System categories cannot be modified!");
        }

        if(!category.getUser().getId().equals(user.getId())) {
            throw new AccessDeniedException("Access Denied");
        }

        if(!category.getCategoryName().equals(categoryRequestDto.categoryName()) && categoryRepository.existsByUserIdAndCategoryName(user.getId(), categoryRequestDto.categoryName())) {
            throw new DuplicateResourceException("Category already exists for the user");
        }

        categoryMapper.updateCategoryFromDto(categoryRequestDto, category);
        var savedCategory = categoryRepository.save(category);
        return categoryMapper.toCategoryResponseDto(savedCategory);
    }

    public void deleteCategory(Long categoryId) {
        var category = categoryRepository.findById(categoryId).orElseThrow(() -> new ResourceNotFoundException("Category not found"));
        if(category.getIsSystemGenerated()) {
            throw new AccessDeniedException("System categories cannot be deleted");
        }

        var user =  authService.getCurrentUser();
        if(!category.getUser().getId().equals(user.getId())) {
            throw new AccessDeniedException("Access Denied");
        }

        category.setDeletedAt(OffsetDateTime.now());
        categoryRepository.save(category);
    }
}

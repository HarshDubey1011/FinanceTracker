package com.hd.FinanceTracker.category.controller;

import com.hd.FinanceTracker.category.dto.CategoryRequestDto;
import com.hd.FinanceTracker.category.dto.CategoryResponseDto;
import com.hd.FinanceTracker.category.service.CategoryService;
import com.hd.FinanceTracker.common.dto.ApiResponse;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/categories")
@AllArgsConstructor
public class CategoryController {
    private final CategoryService categoryService;

    @PostMapping
    public ResponseEntity<ApiResponse<CategoryResponseDto>> createCategory(@Valid @RequestBody CategoryRequestDto categoryRequestDto) {
        var categoryDto = categoryService.createCategory(categoryRequestDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(
                ApiResponse.success("Category created successfully", categoryDto)
        );
    }

    @GetMapping
    public ResponseEntity<ApiResponse<Page<CategoryResponseDto>>> getAllCategory(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size) {
        size = Math.min(size, 50);
        var categoryPage = categoryService.getAllCategories(page, size);
        return ResponseEntity.ok().body(
                ApiResponse.success("All the categories", categoryPage)
        );
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<CategoryResponseDto>> updateCategory(@PathVariable Long id, @Valid @RequestBody CategoryRequestDto categoryRequestDto) {
        var categoryDto = categoryService.updateCategory(categoryRequestDto, id);
        return ResponseEntity.ok().body(
                ApiResponse.success("Category updated successfully", categoryDto)
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteCategory(@PathVariable Long id) {
        categoryService.deleteCategory(id);
        return ResponseEntity.ok().body(
                ApiResponse.success("Category Deleted Successfully", null)
        );
    }
}

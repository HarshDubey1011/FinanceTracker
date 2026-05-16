package com.hd.FinanceTracker.category.repository;

import com.hd.FinanceTracker.category.entity.Category;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CategoryRepository extends JpaRepository<Category, Long> {

    // Fetch system categories + user's own categories
    @Query("SELECT c FROM Category c where c.isSystemGenerated=true or c.user.id = :userId")
    Page<Category> findAllVisibleToUser(@Param("userId") Long userId, Pageable pageable);

    boolean existsByUserIdAndCategoryName(Long userId, String categoryName);
}

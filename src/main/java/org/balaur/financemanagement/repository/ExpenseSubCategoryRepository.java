package org.balaur.financemanagement.repository;

import org.balaur.financemanagement.model.expense.ExpenseSubCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface ExpenseSubCategoryRepository extends JpaRepository<ExpenseSubCategory, Integer> {
    @Query("SELECT s FROM ExpenseSubCategory s WHERE s.id = :id")
    Optional<ExpenseSubCategory> findById(@Param("id") Long id);
}

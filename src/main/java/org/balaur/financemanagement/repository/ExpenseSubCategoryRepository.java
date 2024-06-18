package org.balaur.financemanagement.repository;

import org.balaur.financemanagement.model.expense.ExpenseSubCategory;
import org.balaur.financemanagement.model.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ExpenseSubCategoryRepository extends JpaRepository<ExpenseSubCategory, Integer> {
    @Query("SELECT s FROM ExpenseSubCategory s WHERE s.id = :id")
    Optional<ExpenseSubCategory> findById(@Param("id") Long id);

    @Query("SELECT s FROM ExpenseSubCategory s WHERE s.user = :user")
    List<ExpenseSubCategory> findAllByUser(@Param("user") User user);
}

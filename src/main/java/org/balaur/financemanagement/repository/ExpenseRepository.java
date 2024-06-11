package org.balaur.financemanagement.repository;

import org.balaur.financemanagement.model.expense.Expense;
import org.balaur.financemanagement.model.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ExpenseRepository extends JpaRepository<Expense, Long> {
    @Query("SELECT e FROM Expense e WHERE e.user = :user")
    List<Expense> findByUser(@Param("user") User user);


    @Query("SELECT e FROM Expense  e WHERE e.recurring = true")
    List<Expense> findRecurringExpenses();
}

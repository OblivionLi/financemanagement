package org.balaur.financemanagement.repository;

import org.balaur.financemanagement.model.expense.Expense;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ExpenseStatsRepository extends JpaRepository<Expense, Long> {
    @Query("SELECT e FROM Expense e WHERE YEAR(e.date) = :year AND e.user.id = :userId")
    List<Expense> findByYear(@Param("year") int year, @Param("userId") Long userId);

    @Query("SELECT e FROM Expense e WHERE YEAR(e.date) = :year AND MONTH(e.date) = :month AND e.user.id = :userId")
    List<Expense> findByMonth(@Param("year") int year, @Param("month") int month, @Param("userId") Long userId);

    @Query("SELECT MIN(YEAR(e.date)) FROM Expense e WHERE e.user.id = :userId")
    Integer findMinYear(@Param("userId") Long userId);

    @Query("SELECT MAX(YEAR(e.date)) FROM Expense e WHERE e.user.id = :userId")
    Integer findMaxYear(@Param("userId") Long userId);
}

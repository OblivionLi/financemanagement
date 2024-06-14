package org.balaur.financemanagement.repository;

import org.balaur.financemanagement.model.income.Income;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface IncomeRepository extends JpaRepository<Income, Long> {
    @Query("SELECT i FROM Income i where i.recurring = true")
    List<Income> findRecurringIncomes();
}

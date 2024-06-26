package org.balaur.financemanagement.repository;

import org.balaur.financemanagement.model.income.Income;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface IncomeStatsRepository extends JpaRepository<Income, Long> {
    @Query("SELECT i FROM Income i WHERE YEAR(i.date) = :year AND i.user.id = :userId")
    List<Income> findByYear(@Param("year") int year, @Param("userId") Long userId);

    @Query("SELECT i FROM Income i WHERE YEAR(i.date) = :year AND MONTH(i.date) = :month AND i.user.id = :userId")
    List<Income> findByMonth(@Param("year") int year, @Param("month") int month, @Param("userId") Long userId);

    @Query("SELECT MIN(YEAR(i.date)) FROM Income i WHERE i.user.id = :userId")
    Integer findMinYear(@Param("userId") Long userId);

    @Query("SELECT MAX(YEAR(i.date)) FROM Income i WHERE i.user.id = :userId")
    Integer findMaxYear(@Param("userId") Long userId);

    @Query("SELECT i FROM Income i WHERE i.user.id = :userId")
    List<Income> findByUserId(@Param("userId") Long id);
}

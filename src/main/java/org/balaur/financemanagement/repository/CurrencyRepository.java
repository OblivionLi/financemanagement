package org.balaur.financemanagement.repository;

import org.balaur.financemanagement.model.currency.Currency;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface CurrencyRepository extends JpaRepository<Currency, Integer> {
    Optional<Currency> findByCode(String code);

    @Query("SELECT c FROM Currency c WHERE c.id = :id")
    Optional<Currency> findById(@Param("id") Long id);
}

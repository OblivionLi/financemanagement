package org.balaur.financemanagement.response.stats;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Map;

@Data
@Builder
public class YearlyFinancialSummary {
    private Map<Integer, BigDecimal> monthlyExpenses;
    private Map<Integer, BigDecimal> monthlyIncomes;
    private Map<Integer, Long> monthlyExpenseTransactions;
    private Map<Integer, Long> monthlyIncomeTransactions;
    private Integer minYear;
    private Integer maxYear;
}

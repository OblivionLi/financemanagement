package org.balaur.financemanagement.response.stats;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Map;

@Data
@Builder
public class MonthlyFinancialSummary {
    private Map<Integer, BigDecimal> dailyExpenses;
    private Map<Integer, BigDecimal> dailyIncomes;
    private Map<Integer, Long> dailyExpenseTransactions;
    private Map<Integer, Long> dailyIncomeTransactions;
}

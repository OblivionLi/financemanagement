package org.balaur.financemanagement.response.stats;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class ComparisonSummary {
    private BigDecimal currentMonthExpenses;
    private BigDecimal previousMonthExpenses;
    private BigDecimal currentMonthIncomes;
    private BigDecimal previousMonthIncomes;

    private BigDecimal currentYearExpenses;
    private BigDecimal previousYearExpenses;
    private BigDecimal currentYearIncomes;
    private BigDecimal previousYearIncomes;
}

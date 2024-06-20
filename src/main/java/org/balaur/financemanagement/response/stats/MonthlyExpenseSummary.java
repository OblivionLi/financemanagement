package org.balaur.financemanagement.response.stats;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.balaur.financemanagement.response.expense.ExpenseResponse;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@Builder
public class MonthlyExpenseSummary {
    private List<ExpenseResponse> expenses;
    private BigDecimal monthlyTotal;
}

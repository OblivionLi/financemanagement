package org.balaur.financemanagement.response.stats;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.balaur.financemanagement.response.finances.FinancialResponse;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@Builder
public class MonthlyExpenseSummary {
    private List<FinancialResponse> records;
    private BigDecimal monthlyTotal;
}

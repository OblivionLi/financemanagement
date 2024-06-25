package org.balaur.financemanagement.response.stats;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.balaur.financemanagement.response.finances.FinancialResponse;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Getter
@Setter
@Builder
public class YearlyExpenseSummary {
    private List<FinancialResponse> records;
    private Map<Integer, BigDecimal> monthlyTotals;
    private BigDecimal yearlyTotal;
}

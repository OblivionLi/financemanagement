package org.balaur.financemanagement.response.stats;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class GrandTotalSummary {
    private BigDecimal totalIncomes;
    private BigDecimal totalExpenses;
    private BigDecimal netBalance;
}

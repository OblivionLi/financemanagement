package org.balaur.financemanagement.response.stats;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Map;

@Data
@Builder
public class CategoryBreakdownSummary {
    private Map<String, BigDecimal> expensesByCategory;
    private Map<String, BigDecimal> incomesBySource;
}

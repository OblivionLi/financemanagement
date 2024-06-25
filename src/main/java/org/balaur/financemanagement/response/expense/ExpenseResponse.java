package org.balaur.financemanagement.response.expense;

import lombok.Builder;
import lombok.Data;
import org.balaur.financemanagement.response.finances.FinancialResponse;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
public class ExpenseResponse implements FinancialResponse {
    private Long id;
    private String username;
    private String description;
    private BigDecimal amount;
    private String category;
    private String subCategory;
    private Long subCategoryId;
    private LocalDateTime date;
    private boolean recurring;
    private String recurrencePeriod;
    private String currencyCode;
}

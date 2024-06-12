package org.balaur.financemanagement.response.expense;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@Getter
public class ExpenseResponse {
    private String username;
    private String description;
    private BigDecimal amount;
    private String category;
    private String subCategory;
    private LocalDateTime date;
    private boolean recurring;
    private String recurrencePeriod;
}

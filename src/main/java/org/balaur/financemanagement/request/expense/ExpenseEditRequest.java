package org.balaur.financemanagement.request.expense;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ExpenseEditRequest {
    private String description;
    private BigDecimal amount;
    private Long subCategoryId;
    private LocalDateTime date;
    private Boolean recurring;
    private String recurrencePeriod;
}

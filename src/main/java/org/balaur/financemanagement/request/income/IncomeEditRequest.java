package org.balaur.financemanagement.request.income;

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
public class IncomeEditRequest {
    private String description;
    private String source;
    private BigDecimal amount;
    private LocalDateTime date;
    private Boolean recurring;
    private String recurrencePeriod;
}

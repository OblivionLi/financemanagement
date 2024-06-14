package org.balaur.financemanagement.response.income;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
public class IncomeResponse {
    private Long id;
    private String username;
    private String description;
    private BigDecimal amount;
    private String source;
    private LocalDateTime date;
    private boolean recurring;
    private String recurrencePeriod;
}

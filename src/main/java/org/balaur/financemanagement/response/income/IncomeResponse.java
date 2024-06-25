package org.balaur.financemanagement.response.income;

import lombok.Builder;
import lombok.Data;
import org.balaur.financemanagement.response.finances.FinancialResponse;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
public class IncomeResponse implements FinancialResponse {
    private Long id;
    private String username;
    private String description;
    private BigDecimal amount;
    private String source;
    private LocalDateTime date;
    private boolean recurring;
    private String recurrencePeriod;
    private String currencyCode;
}

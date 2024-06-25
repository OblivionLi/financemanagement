package org.balaur.financemanagement.response.finances;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public interface FinancialResponse {
    Long getId();
    String getUsername();
    String getDescription();
    BigDecimal getAmount();
    LocalDateTime getDate();
    boolean isRecurring();
    String getRecurrencePeriod();
    String getCurrencyCode();
}

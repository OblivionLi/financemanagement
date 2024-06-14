package org.balaur.financemanagement.request.income;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
public class IncomeRequest {
    @NotNull
    private String description;

    @NotNull
    @DecimalMin(value = "0.0", inclusive = false)
    private BigDecimal amount;

    @NotNull
    private String source;

    @NotNull
    private LocalDateTime date;

    private boolean recurring;

    @Pattern(regexp = "WEEKLY|MONTHLY|YEARLY", message = "Recurrence period must be WEEKLY, MONTHLY, or YEARLY")
    private String recurrencePeriod;
}

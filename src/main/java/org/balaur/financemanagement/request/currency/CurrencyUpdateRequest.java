package org.balaur.financemanagement.request.currency;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CurrencyUpdateRequest {
    @NotNull
    @NotEmpty
    private String currencyCode;
    private boolean convertAmounts;
}

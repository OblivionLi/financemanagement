package org.balaur.financemanagement.response.currency;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UpdatedCurrencyResponse {
    private String currency;
    private String message;
}

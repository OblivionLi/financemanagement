package org.balaur.financemanagement.response;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Builder
@Getter
@Setter
public class CurrencyResponse {
    private Long id;
    private String code;
    private String name;
    private BigDecimal rate;
    private LocalDateTime lastTimeUpdated;
}

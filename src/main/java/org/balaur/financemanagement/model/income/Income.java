package org.balaur.financemanagement.model.income;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.balaur.financemanagement.model.currency.Currency;
import org.balaur.financemanagement.model.user.User;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(
    name = "incomes",
    indexes = {
            @Index(name = "idx_recurring", columnList = "recurring")
    }
)
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Income {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String source;
    private String description;
    private BigDecimal amount;
    private LocalDateTime date;
    private boolean recurring;
    private String recurrencePeriod;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "currency_id", nullable = false)
    private Currency currency;
}

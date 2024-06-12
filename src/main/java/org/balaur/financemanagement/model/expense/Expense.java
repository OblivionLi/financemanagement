package org.balaur.financemanagement.model.expense;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.balaur.financemanagement.model.user.User;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(
        name = "expenses",
        indexes = {
                @Index(name = "idx_recurring", columnList = "recurring")
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Expense {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    private String description;
    private BigDecimal amount;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "subcategory_id", nullable = false)
    private ExpenseSubCategory subCategory;

    private LocalDateTime date;
    private boolean recurring;
    private String recurrencePeriod;
}

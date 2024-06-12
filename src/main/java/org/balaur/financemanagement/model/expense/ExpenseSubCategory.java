package org.balaur.financemanagement.model.expense;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.balaur.financemanagement.model.user.User;
import org.balaur.financemanagement.utils.expense.ExpenseCategory;

@Entity
@Table(name = "expense_subcategories")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ExpenseSubCategory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private ExpenseCategory category;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    private String name;
}

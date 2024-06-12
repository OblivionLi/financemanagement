package org.balaur.financemanagement.request.expense;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.balaur.financemanagement.utils.expense.ExpenseCategory;

@Getter
@Setter
public class ExpenseSubCategoryRequest {
    @NotBlank
    private String name;

    @NotNull
    private ExpenseCategory category;
}

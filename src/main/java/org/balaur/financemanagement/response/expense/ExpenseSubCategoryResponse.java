package org.balaur.financemanagement.response.expense;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
@Setter
public class ExpenseSubCategoryResponse {
    private Long id;
    private String categoryName;
    private String username;
    private String subCategoryName;
}

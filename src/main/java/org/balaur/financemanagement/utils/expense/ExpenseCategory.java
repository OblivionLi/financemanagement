package org.balaur.financemanagement.utils.expense;

import lombok.Getter;

@Getter
public enum ExpenseCategory {
    SUBSCRIPTION("Subscription"),
    FOOD("Food"),
    UTILITIES("Utilities"),
    ENTERTAINMENT("Entertainment"),
    TRANSPORTATION("Transportation"),
    HEALTHCARE("Healthcare"),
    OTHER("Other");

    private final String displayName;

    ExpenseCategory(String displayName) {
        this.displayName = displayName;
    }
}

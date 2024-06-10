package org.balaur.financemanagement.components;

import lombok.RequiredArgsConstructor;
import org.balaur.financemanagement.service.expense.ExpenseCronJobs;
import org.balaur.financemanagement.service.expense.ExpenseService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ScheduledTasks {
    private final ExpenseCronJobs expenseCronJobs;

    @Scheduled(cron = "0 0 0 * * ?")
    public void processRecurringExpensesAndIncomes() {
        expenseCronJobs.processRecurringExpenses();
    }
}
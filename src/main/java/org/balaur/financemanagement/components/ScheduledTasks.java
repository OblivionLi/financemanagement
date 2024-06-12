package org.balaur.financemanagement.components;

import lombok.RequiredArgsConstructor;
import org.balaur.financemanagement.service.expense.ExpenseCronJobs;
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

    // Use this only for testing, make sure you also edit the ExpenseCronJobs->calculateNextOccurrence() and add date.plusMinutes(1);
//    @Scheduled(cron = "0 * * * * ?")
//    public void testProcessRecurringExpensesAndIncomes() {
//        expenseCronJobs.processRecurringExpenses();
//    }
}

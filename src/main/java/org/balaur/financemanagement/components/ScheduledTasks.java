package org.balaur.financemanagement.components;

import lombok.RequiredArgsConstructor;
import org.balaur.financemanagement.service.RateLimitService;
import org.balaur.financemanagement.service.currency.CurrencyService;
import org.balaur.financemanagement.service.expense.ExpenseCronJobs;
import org.springframework.cache.CacheManager;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Objects;

import static org.balaur.financemanagement.service.currency.CurrencyService.RATES_CACHE;

@Component
@RequiredArgsConstructor
public class ScheduledTasks {
    private final ExpenseCronJobs expenseCronJobs;
    private final CurrencyService currencyService;
    private final CacheManager cacheManager;
    private final RateLimitService rateLimitService;

    @Scheduled(cron = "0 0 0 * * ?")
    public void processRecurringExpensesAndIncomes() {
        expenseCronJobs.processRecurringExpenses();
    }

    @Scheduled(cron = "0 0 0 * * ?")
    public void invalidateCache() {
        Objects.requireNonNull(cacheManager.getCache(RATES_CACHE)).clear();
    }

    @Scheduled(cron = "0 0 0 * * ?")
    public void resetDailyLimits() {
        rateLimitService.resetDailyLimits();
    }

    // Use this only for testing, make sure you also edit the ExpenseCronJobs->calculateNextOccurrence() and add date.plusMinutes(1);
//    @Scheduled(cron = "0 * * * * ?")
//    public void testProcessRecurringExpensesAndIncomes() {
//        expenseCronJobs.processRecurringExpenses();
//    }
}

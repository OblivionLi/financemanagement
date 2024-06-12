package org.balaur.financemanagement.service.expense;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.balaur.financemanagement.model.expense.Expense;
import org.balaur.financemanagement.repository.ExpenseRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ExpenseCronJobs {
    private final ExpenseRepository expenseRepository;

    @Transactional
    public void processRecurringExpenses() {
        List<Expense> recurringExpenses = expenseRepository.findRecurringExpenses();

        LocalDateTime now = LocalDateTime.now();

        for (Expense expense : recurringExpenses) {
            LocalDateTime nextOccurrence = calculateNextOccurrence(expense.getDate(), expense.getRecurrencePeriod());

            if (nextOccurrence == null) {
                log.info("Skipping expense with ID {}: Invalid recurrence period", expense.getId());
                continue;
            }

            if (!nextOccurrence.isBefore(now)) {
                log.info("Skipping expense with ID {}: Next occurrence is not before now", expense.getId());
                continue;
            }

            if (nextOccurrence.isBefore(expense.getDate())) {
                log.info("Skipping expense with ID {}: Next occurrence is before the current date", expense.getId());
                continue;
            }

            Expense newExpense = createNewExpense(expense, nextOccurrence);

            try {
                expenseRepository.save(newExpense);

                expense.setDate(nextOccurrence);
                expenseRepository.save(expense);
                log.info("Processed recurring expense with ID {}: Created new expense with ID {}", expense.getId(), newExpense.getId());
            } catch (Exception e) {
                log.error("[ExpenseCronJobs] | Error processing recurring expense for user: {}. Error: {}", expense.getUser().getUsername(), e.getMessage());
            }
        }
    }

    private Expense createNewExpense(Expense expense, LocalDateTime nextOccurrence) {
        Expense newExpense = new Expense();
        newExpense.setUser(expense.getUser());
        newExpense.setDescription(expense.getDescription());
        newExpense.setAmount(expense.getAmount());
        newExpense.setSubCategory(expense.getSubCategory());
        newExpense.setDate(nextOccurrence);
        newExpense.setRecurring(expense.isRecurring());
        newExpense.setRecurrencePeriod(expense.getRecurrencePeriod());
        return newExpense;
    }

    private LocalDateTime calculateNextOccurrence(LocalDateTime date, String recurrencePeriod) {
        return switch (recurrencePeriod) {
            case "WEEKLY" -> date.plusWeeks(1);
            case "MONTHLY" -> date.plusMonths(1);
            case "YEARLY" -> date.plusYears(1);
            default -> null;
        };

        // used for testing
//        return switch (recurrencePeriod) {
//            case "WEEKLY" -> date.plusMinutes(1); // For testing, using minutes instead of weeks
//            case "MONTHLY" -> date.plusMinutes(1); // For testing, using minutes instead of months
//            case "YEARLY" -> date.plusMinutes(1); // For testing, using minutes instead of years
//            default -> null;
//        };
    }
}

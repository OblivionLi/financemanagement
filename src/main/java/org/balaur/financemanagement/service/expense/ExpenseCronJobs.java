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
                continue;
            }

            if (!nextOccurrence.isBefore(now)) {
                continue;
            }

            if (nextOccurrence.isBefore(expense.getDate())) {
                continue;
            }

            Expense newExpense = createNewExpense(expense, nextOccurrence);

            try {
                expenseRepository.save(newExpense);

                expense.setDate(nextOccurrence);
                expenseRepository.save(expense);
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
        newExpense.setCategory(expense.getCategory());
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
    }
}

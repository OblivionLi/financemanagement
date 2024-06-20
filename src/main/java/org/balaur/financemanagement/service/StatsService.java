package org.balaur.financemanagement.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.balaur.financemanagement.model.expense.Expense;
import org.balaur.financemanagement.model.user.User;
import org.balaur.financemanagement.repository.ExpenseStatsRepository;
import org.balaur.financemanagement.response.expense.ExpenseResponse;
import org.balaur.financemanagement.response.stats.MonthlyExpenseSummary;
import org.balaur.financemanagement.response.stats.YearlyExpenseSummary;
import org.balaur.financemanagement.service.user.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class StatsService {
    private final UserService userService;
    private final ExpenseStatsRepository expenseStatsRepository;

    public ResponseEntity<YearlyExpenseSummary> getExpensesByYear(Authentication authentication, int year) {
        User user = userService.getUserFromAuthentication(authentication);
        List<Expense> yearlyExpenses;

        try {
            yearlyExpenses = expenseStatsRepository.findByYear(year, user.getId());
        } catch (Exception e) {
            log.error("[StatsService] {} | Error finding expenses by year and user: {}", new Date(), e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }

        Map<Integer, BigDecimal> monthlyTotals = new HashMap<>();
        BigDecimal yearlyTotal = BigDecimal.ZERO;

        for (Expense expense : yearlyExpenses) {
            int month = expense.getDate().getMonthValue();
            monthlyTotals.put(month, monthlyTotals.getOrDefault(month, BigDecimal.ZERO).add(expense.getAmount()));
            yearlyTotal = yearlyTotal.add(expense.getAmount());
        }

        List<ExpenseResponse> expenseResponses = yearlyExpenses.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());

        YearlyExpenseSummary response = YearlyExpenseSummary.builder()
                .expenses(expenseResponses)
                .monthlyTotals(monthlyTotals)
                .yearlyTotal(yearlyTotal)
                .build();

        return ResponseEntity.ok(response);
    }

    public ResponseEntity<MonthlyExpenseSummary> getExpensesByMonth(Authentication authentication, int year, int month) {
        User user = userService.getUserFromAuthentication(authentication);

        List<Expense> monthlyExpenses;

        try {
            monthlyExpenses = expenseStatsRepository.findByMonth(year, month, user.getId());
        } catch (Exception e) {
            log.error("[StatsService] {} | Error finding expenses by year, month and user: {}", new Date(), e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }

        BigDecimal monthlyTotal = monthlyExpenses.stream()
                .map(Expense::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        List<ExpenseResponse> expenseResponses = monthlyExpenses.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());

        MonthlyExpenseSummary response = MonthlyExpenseSummary.builder()
                .expenses(expenseResponses)
                .monthlyTotal(monthlyTotal)
                .build();

        return ResponseEntity.ok(response);
    }

    public ResponseEntity<Integer> getMinYear(Authentication authentication) {
        User user = userService.getUserFromAuthentication(authentication);

        try {
            int minExpenseYear = expenseStatsRepository.findMinYear(user.getId());
            return ResponseEntity.ok(minExpenseYear);
        } catch (Exception e) {
            log.error("[StatsService] {} | Error finding min expense year: {}", new Date(), e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }

    }

    public ResponseEntity<Integer> getMaxYear(Authentication authentication) {
        User user = userService.getUserFromAuthentication(authentication);

        try {
            int maxExpenseYear = expenseStatsRepository.findMaxYear(user.getId());
            return ResponseEntity.ok(maxExpenseYear);
        } catch (Exception e) {
            log.error("[StatsService] {} | Error finding max expense year: {}", new Date(), e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    private ExpenseResponse convertToResponse(Expense expense) {
        return ExpenseResponse.builder()
                .id(expense.getId())
                .username(expense.getUser().getUsername())
                .description(expense.getDescription())
                .amount(expense.getAmount())
                .category(expense.getSubCategory().getCategory().getDisplayName())
                .subCategory(expense.getSubCategory().getName())
                .subCategoryId(expense.getSubCategory().getId())
                .date(expense.getDate())
                .recurring(expense.isRecurring())
                .recurrencePeriod(expense.getRecurrencePeriod())
                .currencyCode(expense.getCurrency())
                .build();
    }
}

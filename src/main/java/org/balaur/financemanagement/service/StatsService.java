package org.balaur.financemanagement.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.balaur.financemanagement.model.expense.Expense;
import org.balaur.financemanagement.model.income.Income;
import org.balaur.financemanagement.model.user.User;
import org.balaur.financemanagement.repository.ExpenseStatsRepository;
import org.balaur.financemanagement.repository.IncomeStatsRepository;
import org.balaur.financemanagement.response.expense.ExpenseResponse;
import org.balaur.financemanagement.response.finances.FinancialResponse;
import org.balaur.financemanagement.response.income.IncomeResponse;
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
    private final IncomeStatsRepository incomeStatsRepository;
    private final ExpenseStatsRepository expenseStatsRepository;

    public ResponseEntity<YearlyExpenseSummary> getStatsByYearType(Authentication authentication, String type, int year) {
        User user = userService.getUserFromAuthentication(authentication);
        List<?> yearlyRecords;

        try {
            if (type.equalsIgnoreCase("incomes")) {
                yearlyRecords = incomeStatsRepository.findByYear(year, user.getId());
            } else {
                yearlyRecords = expenseStatsRepository.findByYear(year, user.getId());
            }
        } catch (Exception e) {
            log.error("[StatsService] {} | Error finding incomes/expenses by year and user: {}", new Date(), e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }

        Map<Integer, BigDecimal> monthlyTotals = new HashMap<>();
        BigDecimal yearlyTotal = BigDecimal.ZERO;

        List<FinancialResponse> financialResponses = yearlyRecords.stream()
                .map(record -> convertToFinancialResponse(type, record))
                .collect(Collectors.toList());

        for (FinancialResponse response : financialResponses) {
            int month = response.getDate().getMonthValue();
            monthlyTotals.put(month, monthlyTotals.getOrDefault(month, BigDecimal.ZERO).add(response.getAmount()));
            yearlyTotal = yearlyTotal.add(response.getAmount());
        }

        YearlyExpenseSummary response = YearlyExpenseSummary.builder()
                .records(financialResponses)
                .monthlyTotals(monthlyTotals)
                .yearlyTotal(yearlyTotal)
                .build();

        return ResponseEntity.ok(response);
    }


    public ResponseEntity<MonthlyExpenseSummary> getStatsByMonthType(Authentication authentication, String type, int year, int month) {
        User user = userService.getUserFromAuthentication(authentication);

        List<?> monthlyRecords;

        try {
            if (type.equalsIgnoreCase("incomes")) {
                monthlyRecords = incomeStatsRepository.findByMonth(year, month, user.getId());
            } else {
                monthlyRecords = expenseStatsRepository.findByMonth(year, month, user.getId());
            }
        } catch (Exception e) {
            log.error("[StatsService] {} | Error finding incomes/expenses by year, month and user: {}", new Date(), e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }

        BigDecimal monthlyTotal = monthlyRecords.stream()
                .map(record -> (record instanceof Expense) ? ((Expense) record).getAmount() : ((Income) record).getAmount())
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        List<FinancialResponse> financialResponses = monthlyRecords.stream()
                .map(record -> convertToFinancialResponse(type, record))
                .collect(Collectors.toList());

        MonthlyExpenseSummary response = MonthlyExpenseSummary.builder()
                .records(financialResponses)
                .monthlyTotal(monthlyTotal)
                .build();

        return ResponseEntity.ok(response);
    }

    public ResponseEntity<Integer> getMinYear(Authentication authentication, String type) {
        User user = userService.getUserFromAuthentication(authentication);

        try {
            Integer minYear;
            if (type.equalsIgnoreCase("incomes")) {
                minYear = incomeStatsRepository.findMinYear(user.getId());
            } else {
                minYear = expenseStatsRepository.findMinYear(user.getId());
            }

            if (minYear == null) {
                log.warn("[StatsService] No minimum year found for type: {}", type);
                return ResponseEntity.noContent().build();
            }

            return ResponseEntity.ok(minYear);
        } catch (Exception e) {
            log.error("[StatsService] {} | Error finding min income/expense year: {}", new Date(), e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }

    }

    public ResponseEntity<Integer> getMaxYear(Authentication authentication, String type) {
        User user = userService.getUserFromAuthentication(authentication);

        try {
            Integer maxYear;
            if (type.equalsIgnoreCase("incomes")) {
                maxYear = incomeStatsRepository.findMaxYear(user.getId());
            } else {
                maxYear = expenseStatsRepository.findMaxYear(user.getId());
            }

            if (maxYear == null) {
                log.warn("[StatsService] No maximum year found for type: {}", type);
                return ResponseEntity.noContent().build();
            }
            return ResponseEntity.ok(maxYear);
        } catch (Exception e) {
            log.error("[StatsService] {} | Error finding max income/expense year: {}", new Date(), e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    private FinancialResponse convertToFinancialResponse(String type, Object record) {
        if (type.equalsIgnoreCase("incomes")) {
            Income income = (Income) record;
            return IncomeResponse.builder()
                    .id(income.getId())
                    .username(income.getUser().getUsername())
                    .description(income.getDescription())
                    .amount(income.getAmount())
                    .source(income.getSource())
                    .date(income.getDate())
                    .recurring(income.isRecurring())
                    .recurrencePeriod(income.getRecurrencePeriod())
                    .currencyCode(income.getCurrency())
                    .build();
        } else {
            Expense expense = (Expense) record;
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

}

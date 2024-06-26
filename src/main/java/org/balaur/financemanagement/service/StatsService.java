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
import org.balaur.financemanagement.response.stats.*;
import org.balaur.financemanagement.service.user.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
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

    public ResponseEntity<YearlyFinancialSummary> getStatsByYear(Authentication authentication, int year) {
        User user = userService.getUserFromAuthentication(authentication);
        List<Expense> expenses;
        List<Income> incomes;

        try {
            incomes = incomeStatsRepository.findByYear(year, user.getId());
            expenses = expenseStatsRepository.findByYear(year, user.getId());
        } catch (Exception e) {
            log.error("[StatsService] {} | Error finding incomes/expenses by year and user: {}", new Date(), e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }

        Map<Integer, BigDecimal> monthlyExpenses = new HashMap<>();
        Map<Integer, BigDecimal> monthlyIncomes = new HashMap<>();
        Map<Integer, Long> monthlyExpenseTransactions = new HashMap<>();
        Map<Integer, Long> monthlyIncomeTransactions = new HashMap<>();

        Integer minYear = null;
        Integer maxYear = null;

        for (Expense expense : expenses) {
            int expenseYear = expense.getDate().getYear();
            if (minYear == null || expenseYear < minYear) minYear = expenseYear;
            if (maxYear == null || expenseYear > maxYear) maxYear = expenseYear;

            int month = expense.getDate().getMonthValue();
            monthlyExpenses.put(month, monthlyExpenses.getOrDefault(month, BigDecimal.ZERO).add(expense.getAmount()));
            monthlyExpenseTransactions.put(month, monthlyExpenseTransactions.getOrDefault(month, 0L) + 1);
        }

        for (Income income : incomes) {
            int incomeYear = income.getDate().getYear();
            if (minYear == null || incomeYear < minYear) minYear = incomeYear;
            if (maxYear == null || incomeYear > maxYear) maxYear = incomeYear;

            int month = income.getDate().getMonthValue();
            monthlyIncomes.put(month, monthlyIncomes.getOrDefault(month, BigDecimal.ZERO).add(income.getAmount()));
            monthlyIncomeTransactions.put(month, monthlyIncomeTransactions.getOrDefault(month, 0L) + 1);
        }

        YearlyFinancialSummary response = YearlyFinancialSummary.builder()
                .monthlyExpenses(monthlyExpenses)
                .monthlyIncomes(monthlyIncomes)
                .monthlyExpenseTransactions(monthlyExpenseTransactions)
                .monthlyIncomeTransactions(monthlyIncomeTransactions)
                .minYear(minYear)
                .maxYear(maxYear)
                .build();

        return ResponseEntity.ok(response);
    }

    public ResponseEntity<MonthlyFinancialSummary> getStatsByYearAndMonth(Authentication authentication, int year, int month) {
        User user = userService.getUserFromAuthentication(authentication);
        List<Expense> expenses;
        List<Income> incomes;

        try {
            incomes = incomeStatsRepository.findByYear(year, user.getId());
            expenses = expenseStatsRepository.findByYear(year, user.getId());
        } catch (Exception e) {
            log.error("[StatsService] {} | Error finding incomes/expenses by year and user: {}", new Date(), e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }

        Map<Integer, BigDecimal> dailyExpenses = new HashMap<>();
        Map<Integer, BigDecimal> dailyIncomes = new HashMap<>();
        Map<Integer, Long> dailyExpenseTransactions = new HashMap<>();
        Map<Integer, Long> dailyIncomeTransactions = new HashMap<>();

        for (Expense expense : expenses) {
            int day = expense.getDate().getDayOfMonth();
            dailyExpenses.put(day, dailyExpenses.getOrDefault(day, BigDecimal.ZERO).add(expense.getAmount()));
            dailyExpenseTransactions.put(day, dailyExpenseTransactions.getOrDefault(day, 0L) + 1);
        }

        for (Income income : incomes) {
            int day = income.getDate().getDayOfMonth();
            dailyIncomes.put(day, dailyIncomes.getOrDefault(day, BigDecimal.ZERO).add(income.getAmount()));
            dailyIncomeTransactions.put(day, dailyIncomeTransactions.getOrDefault(day, 0L) + 1);
        }

        MonthlyFinancialSummary response = MonthlyFinancialSummary.builder()
                .dailyExpenses(dailyExpenses)
                .dailyIncomes(dailyIncomes)
                .dailyExpenseTransactions(dailyExpenseTransactions)
                .dailyIncomeTransactions(dailyIncomeTransactions)
                .build();

        return ResponseEntity.ok(response);
    }

    public ResponseEntity<CategoryBreakdownSummary> getCategoryBreakdown(Authentication authentication, int year) {
        User user = userService.getUserFromAuthentication(authentication);
        List<Expense> expenses;
        List<Income> incomes;

        try {
            incomes = incomeStatsRepository.findByYear(year, user.getId());
            expenses = expenseStatsRepository.findByYear(year, user.getId());
        } catch (Exception e) {
            log.error("[StatsService] {} | Error finding incomes/expenses by year and user: {}", new Date(), e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }

        Map<String, BigDecimal> expensesByCategory = new HashMap<>();
        Map<String, BigDecimal> incomesBySource = new HashMap<>();

        for (Expense expense : expenses) {
            String category = expense.getSubCategory().getCategory().getDisplayName();
            expensesByCategory.put(category, expensesByCategory.getOrDefault(category, BigDecimal.ZERO).add(expense.getAmount()));
        }

        for (Income income : incomes) {
            String source = income.getSource();
            incomesBySource.put(source, incomesBySource.getOrDefault(source, BigDecimal.ZERO).add(income.getAmount()));
        }

        CategoryBreakdownSummary response = CategoryBreakdownSummary.builder()
                .expensesByCategory(expensesByCategory)
                .incomesBySource(incomesBySource)
                .build();

        return ResponseEntity.ok(response);
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

    public ResponseEntity<ComparisonSummary> getComparisonData(Authentication authentication, int year, int month) {
        User user = userService.getUserFromAuthentication(authentication);
        List<Expense> currentMonthExpenses;
        List<Income> currentMonthIncomes;
        List<Expense> previousMonthExpenses;
        List<Income> previousMonthIncomes;
        List<Expense> currentYearExpenses;
        List<Income> currentYearIncomes;
        List<Expense> previousYearExpenses;
        List<Income> previousYearIncomes;

        try {
            currentMonthExpenses = expenseStatsRepository.findByMonth(year, month, user.getId());
            currentMonthIncomes = incomeStatsRepository.findByMonth(year, month, user.getId());

            previousMonthExpenses = expenseStatsRepository.findByMonth(year, month == 1 ? 12 : month - 1, user.getId());
            previousMonthIncomes = incomeStatsRepository.findByMonth(year, month == 1 ? 12 : month - 1, user.getId());

            currentYearExpenses = expenseStatsRepository.findByYear(year, user.getId());
            currentYearIncomes = incomeStatsRepository.findByYear(year, user.getId());

            previousYearExpenses = expenseStatsRepository.findByYear(year - 1, user.getId());
            previousYearIncomes = incomeStatsRepository.findByYear(year - 1, user.getId());
        } catch (Exception e) {
            log.error("[StatsService] {} | Error finding incomes/expenses for comparison data: {}", new Date(), e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }

        BigDecimal currentMonthExpenseTotal = currentMonthExpenses.stream()
                .map(Expense::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal currentMonthIncomeTotal = currentMonthIncomes.stream()
                .map(Income::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal previousMonthExpenseTotal = previousMonthExpenses.stream()
                .map(Expense::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal previousMonthIncomeTotal = previousMonthIncomes.stream()
                .map(Income::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal currentYearExpenseTotal = currentYearExpenses.stream()
                .map(Expense::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal currentYearIncomeTotal = currentYearIncomes.stream()
                .map(Income::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal previousYearExpenseTotal = previousYearExpenses.stream()
                .map(Expense::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal previousYearIncomeTotal = previousYearIncomes.stream()
                .map(Income::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        ComparisonSummary response = ComparisonSummary.builder()
                .currentMonthExpenses(currentMonthExpenseTotal)
                .previousMonthExpenses(previousMonthExpenseTotal)
                .currentMonthIncomes(currentMonthIncomeTotal)
                .previousMonthIncomes(previousMonthIncomeTotal)
                .currentYearExpenses(currentYearExpenseTotal)
                .previousYearExpenses(previousYearExpenseTotal)
                .currentYearIncomes(currentYearIncomeTotal)
                .previousYearIncomes(previousYearIncomeTotal)
                .build();

        return ResponseEntity.ok(response);
    }

    public ResponseEntity<SavingsSummary> getSavingsRateData(Authentication authentication, int year) {
        User user = userService.getUserFromAuthentication(authentication);
        List<Expense> expenses;
        List<Income> incomes;

        try {
            incomes = incomeStatsRepository.findByYear(year, user.getId());
            expenses = expenseStatsRepository.findByYear(year, user.getId());
        } catch (Exception e) {
            log.error("[StatsService] {} | Error finding incomes/expenses for savings rate data: {}", new Date(), e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }

        Map<Integer, BigDecimal> monthlySavingsRate = new HashMap<>();
        Map<Integer, BigDecimal> monthlyIncomeTotals = new HashMap<>();
        Map<Integer, BigDecimal> monthlyExpenseTotals = new HashMap<>();

        for (Income income : incomes) {
            int month = income.getDate().getMonthValue();
            monthlyIncomeTotals.put(month, monthlyIncomeTotals.getOrDefault(month, BigDecimal.ZERO).add(income.getAmount()));
        }

        for (Expense expense : expenses) {
            int month = expense.getDate().getMonthValue();
            monthlyExpenseTotals.put(month, monthlyExpenseTotals.getOrDefault(month, BigDecimal.ZERO).add(expense.getAmount()));
        }

        for (int month = 1; month <= 12; month++) {
            BigDecimal income = monthlyIncomeTotals.getOrDefault(month, BigDecimal.ZERO);
            BigDecimal expense = monthlyExpenseTotals.getOrDefault(month, BigDecimal.ZERO);
            if (income.compareTo(BigDecimal.ZERO) > 0) {
                BigDecimal savingsRate = income.subtract(expense).divide(income, 2, RoundingMode.HALF_UP).multiply(new BigDecimal("100"));
                monthlySavingsRate.put(month, savingsRate);
            } else {
                monthlySavingsRate.put(month, BigDecimal.ZERO);
            }
        }

        SavingsSummary response = SavingsSummary.builder()
                .monthlySavingsRate(monthlySavingsRate)
                .build();

        return ResponseEntity.ok(response);
    }

    public ResponseEntity<GrandTotalSummary> getGrandTotals(Authentication authentication) {
        User user = userService.getUserFromAuthentication(authentication);
        List<Expense> expenses;
        List<Income> incomes;

        try {
            incomes = incomeStatsRepository.findByUserId(user.getId());
            expenses = expenseStatsRepository.findByUserId(user.getId());
        } catch (Exception e) {
            log.error("[StatsService] {} | Error finding incomes/expenses for grand totals: {}", new Date(), e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }

        BigDecimal totalIncomes = incomes.stream().map(Income::getAmount).reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal totalExpenses = expenses.stream().map(Expense::getAmount).reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal netBalance = totalIncomes.subtract(totalExpenses);

        GrandTotalSummary response = GrandTotalSummary.builder()
                .totalIncomes(totalIncomes)
                .totalExpenses(totalExpenses)
                .netBalance(netBalance)
                .build();

        return ResponseEntity.ok(response);
    }
}

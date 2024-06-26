package org.balaur.financemanagement.controller.stats;

import lombok.RequiredArgsConstructor;
import org.balaur.financemanagement.response.stats.*;
import org.balaur.financemanagement.service.StatsService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/stats")
@RequiredArgsConstructor
public class StatsController {
    private final StatsService statsService;

    @GetMapping("/{type}/year/{year}")
    public ResponseEntity<YearlyExpenseSummary> getStatsByYear(Authentication authentication, @PathVariable String type, @PathVariable int year) {
        return statsService.getStatsByYearType(authentication, type, year);
    }

    @GetMapping("/{type}/year/{year}/month/{month}")
    public ResponseEntity<MonthlyExpenseSummary> getStatsByMonth(Authentication authentication, @PathVariable String type, @PathVariable int year, @PathVariable int month) {
        return statsService.getStatsByMonthType(authentication, type, year, month);
    }

    @GetMapping("/{type}/min-year")
    public ResponseEntity<Integer> getMinYear(Authentication authentication, @PathVariable String type) {
        return statsService.getMinYear(authentication, type);
    }

    @GetMapping("/{type}/max-year")
    public ResponseEntity<Integer> getMaxYear(Authentication authentication, @PathVariable String type) {
        return statsService.getMaxYear(authentication, type);
    }

    @GetMapping("/year/{year}")
    public ResponseEntity<YearlyFinancialSummary> getStatsByYear(Authentication authentication, @PathVariable int year) {
        return statsService.getStatsByYear(authentication, year);
    }

    @GetMapping("/year/{year}/month/{month}")
    public ResponseEntity<MonthlyFinancialSummary> getStatsByYearAndMonth(Authentication authentication, @PathVariable int year, @PathVariable int month) {
        return statsService.getStatsByYearAndMonth(authentication, year, month);
    }

    @GetMapping("/year/{year}/category-breakdown")
    public ResponseEntity<CategoryBreakdownSummary> getCategoryStatsByYear(Authentication authentication, @PathVariable int year) {
        return statsService.getCategoryBreakdown(authentication, year);
    }

    @GetMapping("/year/{year}/month/{month}/comparison")
    public ResponseEntity<ComparisonSummary> getComparisonData(Authentication authentication, @PathVariable int year, @PathVariable int month) {
        return statsService.getComparisonData(authentication, year, month);
    }

    @GetMapping("/year/{year}/savings-rate")
    public ResponseEntity<SavingsSummary> getSavingsRate(Authentication authentication, @PathVariable int year) {
        return statsService.getSavingsRateData(authentication, year);
    }

    @GetMapping("/grand-totals")
    public ResponseEntity<GrandTotalSummary> getGrandTotals(Authentication authentication) {
        return statsService.getGrandTotals(authentication);
    }
}

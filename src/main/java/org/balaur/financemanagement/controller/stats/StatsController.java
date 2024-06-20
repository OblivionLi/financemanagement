package org.balaur.financemanagement.controller.stats;

import lombok.RequiredArgsConstructor;
import org.balaur.financemanagement.response.stats.MonthlyExpenseSummary;
import org.balaur.financemanagement.response.stats.YearlyExpenseSummary;
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

    @GetMapping("/expenses/year/{year}")
    public ResponseEntity<YearlyExpenseSummary> getExpensesByYear(Authentication authentication, @PathVariable int year) {
        return statsService.getExpensesByYear(authentication, year);
    }

    @GetMapping("/expenses/year/{year}/month/{month}")
    public ResponseEntity<MonthlyExpenseSummary> getExpensesByMonth(Authentication authentication, @PathVariable int year, @PathVariable int month) {
        return statsService.getExpensesByMonth(authentication, year, month);
    }

    @GetMapping("/expenses/min-year")
    public ResponseEntity<Integer> getMinYear(Authentication authentication) {
        return statsService.getMinYear(authentication);
    }

    @GetMapping("/expenses/max-year")
    public ResponseEntity<Integer> getMaxYear(Authentication authentication) {
        return statsService.getMaxYear(authentication);
    }
}

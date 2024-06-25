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
}

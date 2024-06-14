package org.balaur.financemanagement.controller.income;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.balaur.financemanagement.request.income.IncomeEditRequest;
import org.balaur.financemanagement.request.income.IncomeRequest;
import org.balaur.financemanagement.response.income.IncomeResponse;
import org.balaur.financemanagement.service.income.IncomeService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/incomes")
@RequiredArgsConstructor
public class IncomeController {
    private final IncomeService incomeService;

    @GetMapping
    public ResponseEntity<List<IncomeResponse>> findAllIncomes() {
        return incomeService.findAllIncomes();
    }

    @GetMapping("/recurring")
    public ResponseEntity<List<IncomeResponse>> findAllRecurringIncomes() {
        return incomeService.findAllRecurringIncomes();
    }

    @PostMapping("/add")
    public ResponseEntity<IncomeResponse> addIncome(Authentication authentication, @Valid @RequestBody IncomeRequest incomeRequest) {
        return incomeService.addIncome(authentication, incomeRequest);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> deleteIncome(Authentication authentication, @PathVariable Long id) {
        return incomeService.deleteIncome(authentication, id);
    }

    @PatchMapping("/edit/{id}")
    public ResponseEntity<IncomeResponse> editExpense(Authentication authentication, @PathVariable Long id, @RequestBody IncomeEditRequest incomeEditRequest) {
        return incomeService.editIncome(authentication, id, incomeEditRequest);
    }
}

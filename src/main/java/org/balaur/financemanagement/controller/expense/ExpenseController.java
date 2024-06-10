package org.balaur.financemanagement.controller.expense;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.balaur.financemanagement.request.expense.ExpenseRequest;
import org.balaur.financemanagement.response.expense.ExpenseResponse;
import org.balaur.financemanagement.service.expense.ExpenseService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/expenses")
@RequiredArgsConstructor
public class ExpenseController {
    private ExpenseService expenseService;

    @PostMapping("/add")
    public ResponseEntity<ExpenseResponse> addExpense(Authentication authentication, @Valid @RequestBody ExpenseRequest expenseRequest) {
        return expenseService.addExpense(authentication, expenseRequest);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> deleteExpense(Authentication authentication, @PathVariable Long id) {
        return expenseService.deleteExpense(authentication, id);
    }
}

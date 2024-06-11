package org.balaur.financemanagement.controller.expense;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.balaur.financemanagement.request.expense.ExpenseEditRequest;
import org.balaur.financemanagement.request.expense.ExpenseRequest;
import org.balaur.financemanagement.response.expense.ExpenseResponse;
import org.balaur.financemanagement.service.expense.ExpenseService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/expenses")
@RequiredArgsConstructor
public class ExpenseController {
    private final ExpenseService expenseService;

    @GetMapping
    public ResponseEntity<List<ExpenseResponse>> findAllExpenses() {
        return expenseService.findAllExpenses();
    }

    @PostMapping("/add")
    public ResponseEntity<ExpenseResponse> addExpense(Authentication authentication, @Valid @RequestBody ExpenseRequest expenseRequest) {
        return expenseService.addExpense(authentication, expenseRequest);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> deleteExpense(Authentication authentication, @PathVariable Long id) {
        return expenseService.deleteExpense(authentication, id);
    }

    @PatchMapping("/edit/{id}")
    public ResponseEntity<ExpenseResponse> editExpense(Authentication authentication, @PathVariable Long id, @RequestBody ExpenseEditRequest expenseEditRequest) {
        return expenseService.editExpense(authentication, id, expenseEditRequest);
    }

    @GetMapping("/recurring")
    public ResponseEntity<List<ExpenseResponse>> findAllRecurringExpenses() {
        return expenseService.findAllRecurringExpenses();
    }
}

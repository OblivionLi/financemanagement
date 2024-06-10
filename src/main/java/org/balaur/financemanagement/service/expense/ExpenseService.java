package org.balaur.financemanagement.service.expense;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.balaur.financemanagement.model.expense.Expense;
import org.balaur.financemanagement.model.user.User;
import org.balaur.financemanagement.repository.ExpenseRepository;
import org.balaur.financemanagement.repository.UserRepository;
import org.balaur.financemanagement.request.expense.ExpenseRequest;
import org.balaur.financemanagement.response.auth.AuthResponse;
import org.balaur.financemanagement.response.expense.ExpenseResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
@RequiredArgsConstructor
@Slf4j
public class ExpenseService {
    private final ExpenseRepository expenseRepository;
    private final UserRepository userRepository;

    public ResponseEntity<ExpenseResponse> addExpense(Authentication authentication, @Valid ExpenseRequest expenseRequest) {
        User user = getUserFromAuthentication(authentication);

        Expense expense = getExpense(expenseRequest, user);

        try {
            expense = expenseRepository.save(expense);
        } catch (Exception e) {
            log.error("[ExpenseService] {} | Error saving expense: {}", new Date(), e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }

        ExpenseResponse expenseResponse = ExpenseResponse.builder()
                .username(expense.getUser().getUsername())
                .description(expense.getDescription())
                .amount(expense.getAmount())
                .category(expense.getCategory())
                .date(expense.getDate())
                .recurring(expense.isRecurring())
                .recurrencePeriod(expense.getRecurrencePeriod())
                .build();

        return ResponseEntity.status(HttpStatus.CREATED).body(expenseResponse);
    }

    private static Expense getExpense(ExpenseRequest expenseRequest, User user) {
        Expense expense = new Expense();

        expense.setUser(user);
        expense.setAmount(expenseRequest.getAmount());
        expense.setDescription(expenseRequest.getDescription());
        expense.setCategory(expenseRequest.getCategory());
        expense.setDate(expenseRequest.getDate());

        if (expenseRequest.isRecurring()) {
            expense.setRecurring(true);
            expense.setRecurrencePeriod(expenseRequest.getRecurrencePeriod());
        } else {
            expense.setRecurring(false);
        }
        return expense;
    }

    public ResponseEntity<String> deleteExpense(Authentication authentication, Long id) {
        User user = getUserFromAuthentication(authentication);

        try {
            Expense expense = expenseRepository.findById(id)
                    .orElseThrow(() -> new IllegalArgumentException("Expense not found with id: " + id));

            if (!expense.getUser().equals(user)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }

            String expenseDescription = "Expense deleted with description: " + expense.getDescription();

            expenseRepository.delete(expense);

            return ResponseEntity.status(HttpStatus.OK).body(expenseDescription);

        } catch (Exception e) {
            log.error("[ExpenseService] {} | Error deleting expense: {}", new Date(), e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    private User getUserFromAuthentication(Authentication authentication) {
        String email = ((AuthResponse) authentication.getPrincipal()).getEmail();
        User user = userRepository.findByEmail(email);

        if (user == null) {
            log.warn("[ExpenseService] {} | User: {} not found.", new Date(), authentication.getName());
            throw new UsernameNotFoundException("User: " + authentication.getName() + " not found.");
        }
        return user;
    }
}

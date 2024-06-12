package org.balaur.financemanagement.service.expense;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.balaur.financemanagement.model.expense.ExpenseSubCategory;
import org.balaur.financemanagement.model.user.User;
import org.balaur.financemanagement.repository.ExpenseSubCategoryRepository;
import org.balaur.financemanagement.request.expense.ExpenseSubCategoryRequest;
import org.balaur.financemanagement.response.expense.ExpenseSubCategoryResponse;
import org.balaur.financemanagement.service.user.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
@RequiredArgsConstructor
@Slf4j
public class ExpenseSubCategoryService {
    private final ExpenseSubCategoryRepository subCategoryRepository;
    private final UserService userService;

    public ResponseEntity<ExpenseSubCategoryResponse> createSubCategory(Authentication authentication, ExpenseSubCategoryRequest request) {
        User user = userService.getUserFromAuthentication(authentication);

        ExpenseSubCategory subCategory = new ExpenseSubCategory();
        subCategory.setUser(user);
        subCategory.setCategory(request.getCategory());
        subCategory.setName(request.getName());

        try {
            subCategory = subCategoryRepository.save(subCategory);
        } catch (Exception e) {
            log.error("[ExpenseService] {} | Error saving expense: {}", new Date(), e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }

        ExpenseSubCategoryResponse response = ExpenseSubCategoryResponse.builder()
                .categoryName(subCategory.getCategory().getDisplayName())
                .subCategoryName(subCategory.getName())
                .username(subCategory.getUser().getUsername())
                .build();

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}

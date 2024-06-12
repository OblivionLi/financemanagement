package org.balaur.financemanagement.controller.expense;

import lombok.RequiredArgsConstructor;
import org.balaur.financemanagement.request.expense.ExpenseSubCategoryRequest;
import org.balaur.financemanagement.response.expense.ExpenseSubCategoryResponse;
import org.balaur.financemanagement.service.expense.ExpenseSubCategoryService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/expenses")
@RequiredArgsConstructor
public class ExpenseSubCategoryController {
    private final ExpenseSubCategoryService subCategoryService;

    @PostMapping("/subcategories/add")
    public ResponseEntity<ExpenseSubCategoryResponse> addSubCategory(Authentication authentication, @RequestBody ExpenseSubCategoryRequest subCategoryRequest) {
        return subCategoryService.createSubCategory(authentication, subCategoryRequest);
    }
}

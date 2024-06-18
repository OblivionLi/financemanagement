package org.balaur.financemanagement.controller.expense;

import lombok.RequiredArgsConstructor;
import org.balaur.financemanagement.request.expense.ExpenseSubCategoryRequest;
import org.balaur.financemanagement.response.expense.ExpenseSubCategoryResponse;
import org.balaur.financemanagement.service.expense.ExpenseSubCategoryService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/expenses")
@RequiredArgsConstructor
public class ExpenseSubCategoryController {
    private final ExpenseSubCategoryService subCategoryService;

    @PostMapping("/subcategories/add")
    public ResponseEntity<ExpenseSubCategoryResponse> addSubCategory(Authentication authentication, @RequestBody ExpenseSubCategoryRequest subCategoryRequest) {
        return subCategoryService.createSubCategory(authentication, subCategoryRequest);
    }

    @GetMapping("/subcategories")
    public ResponseEntity<List<ExpenseSubCategoryResponse>> getUserSubCategories(Authentication authentication) {
        return subCategoryService.getUserSubCategories(authentication);
    }
}

package org.balaur.financemanagement.service;

import org.balaur.financemanagement.model.expense.ExpenseSubCategory;
import org.balaur.financemanagement.model.user.User;
import org.balaur.financemanagement.repository.ExpenseSubCategoryRepository;
import org.balaur.financemanagement.request.expense.ExpenseSubCategoryRequest;
import org.balaur.financemanagement.response.expense.ExpenseSubCategoryResponse;
import org.balaur.financemanagement.service.expense.ExpenseSubCategoryService;
import org.balaur.financemanagement.service.user.UserService;
import org.balaur.financemanagement.utils.expense.ExpenseCategory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@SpringBootTest
@ActiveProfiles("test")
public class ExpenseSubCategoryTest {

    @MockBean
    private ExpenseSubCategoryRepository subCategoryRepository;

    @MockBean
    private UserService userService;

    @MockBean
    private Authentication authentication;

    @Autowired
    private ExpenseSubCategoryService expenseSubCategoryService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("Test expense subcategory creation with success.")
    @WithMockUser(username = "JohnDoe", roles = {"USER"})
    void ExpenseSubCategoryService_CreateSubCategory_Success() {
        ExpenseSubCategoryRequest request = new ExpenseSubCategoryRequest();
        request.setCategory(ExpenseCategory.UTILITIES);
        request.setName("Sample Name");

        User user = getMockUser();

        when(userService.getUserFromAuthentication(authentication)).thenReturn(user);
        when(subCategoryRepository.save(any(ExpenseSubCategory.class))).thenAnswer(invocation -> {
            ExpenseSubCategory arg = invocation.getArgument(0);
            arg.setId(1L); // Simulate database generated ID
            return arg;
        });

        ResponseEntity<ExpenseSubCategoryResponse> result = expenseSubCategoryService.createSubCategory(authentication, request);

        assertEquals(HttpStatus.CREATED, result.getStatusCode());
        assertEquals("Utilities", Objects.requireNonNull(result.getBody()).getCategoryName());
        assertEquals("Sample Name", Objects.requireNonNull(result.getBody()).getSubCategoryName());
        assertEquals("JohnDoe", result.getBody().getUsername());
    }

    @Test
    @DisplayName("Test expense subcategory creation failure.")
    @WithMockUser(username = "JohnDoe", roles = {"USER"})
    void ExpenseSubCategoryService_CreateSubCategory_Failure() {
        User user = getMockUser();

        ExpenseSubCategoryRequest request = new ExpenseSubCategoryRequest();
        request.setCategory(ExpenseCategory.UTILITIES);
        request.setName("Sample Name");

        when(userService.getUserFromAuthentication(authentication)).thenReturn(user);
        when(subCategoryRepository.save(any(ExpenseSubCategory.class))).thenThrow(new RuntimeException("Database Error"));

        ResponseEntity<ExpenseSubCategoryResponse> result = expenseSubCategoryService.createSubCategory(authentication, request);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, result.getStatusCode());
    }

    // ==================== Objects Mocks ====================
    private User getMockUser() {
        User user = new User();
        user.setId(1L);
        user.setUsername("JohnDoe");
        user.setEmail("john.doe@example.com");
        user.setHashedPassword("encodedPassword");
        user.setLocked(false);
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());

        return user;
    }
}
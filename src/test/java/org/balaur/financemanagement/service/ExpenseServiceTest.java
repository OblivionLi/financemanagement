package org.balaur.financemanagement.service;

import org.balaur.financemanagement.model.expense.Expense;
import org.balaur.financemanagement.model.expense.ExpenseSubCategory;
import org.balaur.financemanagement.model.user.User;
import org.balaur.financemanagement.repository.ExpenseRepository;
import org.balaur.financemanagement.repository.ExpenseSubCategoryRepository;
import org.balaur.financemanagement.request.expense.ExpenseEditRequest;
import org.balaur.financemanagement.request.expense.ExpenseRequest;
import org.balaur.financemanagement.response.expense.ExpenseResponse;
import org.balaur.financemanagement.service.expense.ExpenseService;
import org.balaur.financemanagement.service.user.UserService;
import org.balaur.financemanagement.utils.expense.ExpenseCategory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest
@ActiveProfiles("test")
public class ExpenseServiceTest {
    private static final Logger log = LoggerFactory.getLogger(ExpenseServiceTest.class);
    @MockBean
    private ExpenseRepository expenseRepository;

    @MockBean
    private UserService userService;

    @MockBean
    private Authentication authentication;

    @MockBean
    private ExpenseSubCategoryRepository subCategoryRepository;

    @Autowired
    private ExpenseService expenseService;

    private User mockUser;
    private ExpenseSubCategory mockSubCategory;
    private ExpenseRequest mockExpenseRequest;
    private Expense mockExpense;
    private ExpenseEditRequest mockExpenseEditRequest;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        mockUser = getMockUser();
        mockSubCategory = getMockSubCategory(mockUser);
        mockExpenseRequest = getMockExpenseRequest();
        mockExpense = getExpense();
        mockExpenseEditRequest = getMockExpenseEditRequest();
    }

    @Test
    @DisplayName("Test adding an expense successfully")
    void ExpenseService_AddExpense_Success() {
        when(userService.getUserFromAuthentication(authentication)).thenReturn(mockUser);
        when(subCategoryRepository.findById(1L)).thenReturn(Optional.ofNullable(mockSubCategory));
        when(expenseRepository.save(any(Expense.class))).thenAnswer(invocation -> {
            Expense expense = invocation.getArgument(0);
            expense.setId(1L);
            return expense;
        });

        ResponseEntity<ExpenseResponse> response = expenseService.addExpense(authentication, mockExpenseRequest);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals("Test Expense", Objects.requireNonNull(response.getBody()).getDescription());
        assertEquals("Electricity Payment", response.getBody().getSubCategory());
    }

    @Test
    @DisplayName("Test adding an expense with invalid subcategory")
    void ExpenseService_AddExpenseInvalidSubCategory_Success() {
        when(userService.getUserFromAuthentication(authentication)).thenReturn(mockUser);
        when(subCategoryRepository.findById(2L)).thenReturn(Optional.empty());

        ResponseEntity<ExpenseResponse> response = expenseService.addExpense(authentication, mockExpenseRequest);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    @DisplayName("Test deleting an expense successfully")
    void ExpenseService_DeleteExpense_Success() {
        when(userService.getUserFromAuthentication(authentication)).thenReturn(mockUser);
        when(expenseRepository.findById(1L)).thenReturn(Optional.of(mockExpense));

        ResponseEntity<String> response = expenseService.deleteExpense(authentication, 1L);

        String expectedMessage = "Expense deleted with description: Test Expense";
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(expectedMessage, response.getBody());

        verify(expenseRepository, times(1)).delete(mockExpense);
    }

    @Test
    @DisplayName("Test deleting an expense not found")
    void ExpenseService_DeleteExpenseNotFound() {
        when(userService.getUserFromAuthentication(authentication)).thenReturn(mockUser);
        when(expenseRepository.findById(1L)).thenReturn(Optional.empty());

        ResponseEntity<String> response = expenseService.deleteExpense(authentication, 1L);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    }

    @Test
    @DisplayName("Test editing an expense successfully")
    void ExpenseService_EditExpense() {
        when(userService.getUserFromAuthentication(authentication)).thenReturn(mockUser);
        when(expenseRepository.findById(1L)).thenReturn(Optional.of(mockExpense));
        when(expenseRepository.save(any(Expense.class))).thenReturn(mockExpense);
        when(subCategoryRepository.findById(1L)).thenReturn(Optional.ofNullable(mockSubCategory));

        ResponseEntity<ExpenseResponse> response = expenseService.editExpense(authentication, 1L, mockExpenseEditRequest);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Updated Expense", Objects.requireNonNull(response.getBody()).getDescription());
        assertEquals(BigDecimal.valueOf(100), response.getBody().getAmount());
    }

    @Test
    @DisplayName("Test editing an expense not found")
    void ExpenseService_EditExpenseNotFound() {
        when(userService.getUserFromAuthentication(authentication)).thenReturn(mockUser);
        when(expenseRepository.findById(1L)).thenReturn(Optional.empty());

        ResponseEntity<ExpenseResponse> response = expenseService.editExpense(authentication, 1L, mockExpenseEditRequest);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
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

    private ExpenseSubCategory getMockSubCategory(User user) {
        ExpenseSubCategory subCategory = new ExpenseSubCategory();
        subCategory.setId(1L);
        subCategory.setCategory(ExpenseCategory.UTILITIES);
        subCategory.setUser(user);
        subCategory.setName("Electricity Payment");


        return subCategory;
    }

    private ExpenseRequest getMockExpenseRequest() {
        ExpenseRequest request = new ExpenseRequest();
        request.setDescription("Test Expense");
        request.setAmount(BigDecimal.valueOf(100));
        request.setSubCategoryId(1L);
        request.setDate(LocalDateTime.now());
        request.setRecurring(false);
        request.setRecurrencePeriod("MONTHLY");

        return request;
    }

    private ExpenseEditRequest getMockExpenseEditRequest() {
        return new ExpenseEditRequest(
                "Updated Expense",
                BigDecimal.valueOf(100),
                1L,
                LocalDateTime.now(),
                false,
                "MONTHLY"
        );
    }

    private Expense getExpense() {
        Expense expense = new Expense();
        expense.setId(1L);
        expense.setUser(mockUser);
        expense.setDescription("Test Expense");
        return expense;
    }
}

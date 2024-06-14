package org.balaur.financemanagement.service.income;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.balaur.financemanagement.model.income.Income;
import org.balaur.financemanagement.model.user.User;
import org.balaur.financemanagement.repository.IncomeRepository;
import org.balaur.financemanagement.request.income.IncomeEditRequest;
import org.balaur.financemanagement.request.income.IncomeRequest;
import org.balaur.financemanagement.response.income.IncomeResponse;
import org.balaur.financemanagement.service.user.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class IncomeService {
    private final IncomeRepository incomeRepository;
    private final UserService userService;

    public ResponseEntity<List<IncomeResponse>> findAllIncomes() {
        List<Income> incomeList = incomeRepository.findAll();
        return getListResponseEntity(incomeList);
    }

    public ResponseEntity<List<IncomeResponse>> findAllRecurringIncomes() {
        List<Income> incomeList = incomeRepository.findRecurringIncomes();
        return getListResponseEntity(incomeList);
    }

    private ResponseEntity<List<IncomeResponse>> getListResponseEntity(List<Income> incomeList) {
        List<IncomeResponse> incomeResponseList = new ArrayList<>();

        for (Income income : incomeList) {
            IncomeResponse incomeResponse = buildIncomeResponse(income);
            incomeResponse.setUsername(income.getUser().getUsername());

            incomeResponseList.add(incomeResponse);
        }

        return ResponseEntity.ok(incomeResponseList);
    }

    private IncomeResponse buildIncomeResponse(Income income) {
        return IncomeResponse.builder()
                .id(income.getId())
                .description(income.getDescription())
                .amount(income.getAmount())
                .source(income.getSource())
                .date(income.getDate())
                .recurring(income.isRecurring())
                .recurrencePeriod(income.getRecurrencePeriod())
                .build();
    }

    public ResponseEntity<IncomeResponse> addIncome(Authentication authentication, @Valid IncomeRequest incomeRequest) {
        User user = userService.getUserFromAuthentication(authentication);

        Income income = new Income();
        income.setUser(user);
        income.setAmount(incomeRequest.getAmount());
        income.setDescription(incomeRequest.getDescription());
        income.setSource(incomeRequest.getSource());
        income.setDate(incomeRequest.getDate());
        income.setRecurrencePeriod(incomeRequest.getRecurrencePeriod());
        income.setRecurring(incomeRequest.isRecurring());

        try {
            income = incomeRepository.save(income);
        } catch (Exception e) {
            log.error("[IncomeService] {} | Error saving expense: {}", new Date(), e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }

        IncomeResponse incomeResponse = buildIncomeResponse(income);
        incomeResponse.setUsername(income.getUser().getUsername());

        return ResponseEntity.status(HttpStatus.CREATED).body(incomeResponse);
    }


    public ResponseEntity<String> deleteIncome(Authentication authentication, Long id) {
        User user = userService.getUserFromAuthentication(authentication);

        try {
            Income income = incomeRepository.findById(id)
                    .orElseThrow(() -> new IllegalArgumentException("Income not found with id: " + id));

            if (!income.getUser().equals(user)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }

            String incomeDescription = "Income deleted with description: " + income.getDescription();

            incomeRepository.delete(income);

            return ResponseEntity.status(HttpStatus.OK).body(incomeDescription);

        } catch (Exception e) {
            log.error("[IncomeService] {} | Error deleting income: {}", new Date(), e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    public ResponseEntity<IncomeResponse> editIncome(Authentication authentication, Long id, IncomeEditRequest request) {
        User user = userService.getUserFromAuthentication(authentication);

        try {
            Income income = incomeRepository.findById(id)
                    .orElseThrow(() -> new IllegalArgumentException("Income not found with id: " + id));

            if (!income.getUser().equals(user)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }

            updateIncomeFields(income, request);

            incomeRepository.save(income);

            IncomeResponse incomeResponse = buildIncomeResponse(income);
            incomeResponse.setUsername(user.getUsername());

            return ResponseEntity.ok(incomeResponse);
        } catch (IllegalArgumentException e) {
            log.error("[IncomeService] {} | Income not found: {}", new Date(), e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        } catch (Exception e) {
            log.error("[IncomeService] {} | Error updating income: {}", new Date(), e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    private void updateIncomeFields(Income income, IncomeEditRequest request) {
        if (request.getDescription() != null) {
            income.setDescription(request.getDescription());
        }

        if (request.getSource() != null) {
            income.setSource(request.getSource());
        }

        if (request.getAmount() != null) {
            income.setAmount(request.getAmount());
        }

        if (request.getDate() != null) {
            income.setDate(request.getDate());
        }

        if (request.getRecurring() != null) {
            income.setRecurring(request.getRecurring());
        }

        if (request.getRecurrencePeriod() != null) {
            income.setRecurrencePeriod(request.getRecurrencePeriod());
        }

        if (request.getRecurring() != null && !request.getRecurring()) {
            income.setRecurrencePeriod(null);
        }
    }
}

package org.balaur.financemanagement.service.currency;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.balaur.financemanagement.model.expense.Expense;
import org.balaur.financemanagement.model.income.Income;
import org.balaur.financemanagement.model.user.User;
import org.balaur.financemanagement.repository.ExpenseRepository;
import org.balaur.financemanagement.repository.IncomeRepository;
import org.balaur.financemanagement.repository.UserRepository;
import org.balaur.financemanagement.request.currency.CurrencyUpdateRequest;
import org.balaur.financemanagement.service.RateLimitService;
import org.balaur.financemanagement.service.user.UserService;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class CurrencyService {
    private final UserService userService;
    private final UserRepository userRepository;
    private final CacheManager cacheManager;
    public static final String RATES_CACHE = "ratesCache";
    private final IncomeRepository incomeRepository;
    private final ExpenseRepository expenseRepository;
    private final RateLimitService rateLimitService;

    public Map<String, Double> getExchangeRates(String baseCurrency) {
        Cache cache = cacheManager.getCache(RATES_CACHE);
        Cache.ValueWrapper cachedRates = Objects.requireNonNull(cache).get(baseCurrency);

        if (cachedRates != null) {
            return (Map<String, Double>) cachedRates.get();
        }

        String url = "https://api.exchangerate-api.com/v4/latest/" + baseCurrency;
        ResponseEntity<Map<String, Object>> response = new RestTemplate().getForEntity(url, (Class<Map<String, Object>>)(Class<?>)Map.class);

        if (response.getStatusCode() == HttpStatus.OK) {
            Map<String, Double> rates = (Map<String, Double>) response.getBody().get("rates");
            cache.put(baseCurrency, rates);
            return rates;
        } else {
            throw new RuntimeException("Failed to fetch exchange rates");
        }
    }

    public double getExchangeRate(String fromCurrency, String toCurrency) {
        if (fromCurrency.equals(toCurrency)) {
            return 1.0;
        }

        Map<String, Double> rates = getExchangeRates(fromCurrency);
        return rates.get(toCurrency);
    }


    @Transactional
    public ResponseEntity<String> updateCurrency(Authentication authentication, CurrencyUpdateRequest request) {
        User user = userService.getUserFromAuthentication(authentication);

        if (rateLimitService.isRateLimitRequest(user.getEmail())) {
            return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).body("Rate limit exceeded");
        }

        String oldCurrencyCode = user.getPreferredCurrency();

        String code = getCurrencyName(request.getCurrencyCode());
        if (code.equals("Unknown Currency")) {
            return ResponseEntity.badRequest().body(null);
        }

        user.setPreferredCurrency(request.getCurrencyCode());

        try {
            userRepository.save(user);
            rateLimitService.incrementRequestCount(user.getEmail());
        } catch (Exception e) {
            log.error("[CurrencyService] {} | Error updating user preferred currency: {}", new Date(), e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }

        if (!request.isConvertAmounts()) {
            return ResponseEntity.ok("Currency updated successfully");
        }

        double getNewRate = getExchangeRate(oldCurrencyCode, request.getCurrencyCode());

        try {
            updateIncomesAmounts(request.getCurrencyCode(), getNewRate);
            updateExpensesAmounts(request.getCurrencyCode(), getNewRate);

            return ResponseEntity.ok("Currency and amounts updated successfully");
        } catch (Exception e) {
            log.error("[CurrencyService] {} | Error updating incomes/expenses amounts: {}", new Date(), e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @Transactional
    public void updateExpensesAmounts(String currencyCode, double exchangeRate) {
        List<Expense> expenseList = expenseRepository.findAll();
        for (Expense expense : expenseList) {
            BigDecimal newAmount = expense.getAmount().multiply(BigDecimal.valueOf(exchangeRate));
            expense.setAmount(newAmount);
            expense.setCurrency(currencyCode);
            expenseRepository.save(expense);
        }
    }

    @Transactional
    public void updateIncomesAmounts(String currencyCode, double exchangeRate) {
        List<Income> incomeList = incomeRepository.findAll();
        for (Income income : incomeList) {
            BigDecimal newAmount = income.getAmount().multiply(BigDecimal.valueOf(exchangeRate));
            income.setAmount(newAmount);
            income.setCurrency(currencyCode);
            incomeRepository.save(income);
        }
    }

    private String getCurrencyName(String code) {
        Map<String, String> currencyNames = Map.ofEntries(
                Map.entry("USD", "United States Dollar"),
                Map.entry("AED", "United Arab Emirates Dirham"),
                Map.entry("AFN", "Afghan Afghani"),
                Map.entry("ALL", "Albanian Lek"),
                Map.entry("AMD", "Armenian Dram"),
                Map.entry("ANG", "Netherlands Antillean Guilder"),
                Map.entry("AOA", "Angolan Kwanza"),
                Map.entry("ARS", "Argentine Peso"),
                Map.entry("AUD", "Australian Dollar"),
                Map.entry("AWG", "Aruban Florin"),
                Map.entry("AZN", "Azerbaijani Manat"),
                Map.entry("BAM", "Bosnia-Herzegovina Convertible Mark"),
                Map.entry("BBD", "Barbadian Dollar"),
                Map.entry("BDT", "Bangladeshi Taka"),
                Map.entry("BGN", "Bulgarian Lev"),
                Map.entry("BHD", "Bahraini Dinar"),
                Map.entry("BIF", "Burundian Franc"),
                Map.entry("BMD", "Bermudian Dollar"),
                Map.entry("BND", "Brunei Dollar"),
                Map.entry("BOB", "Bolivian Boliviano"),
                Map.entry("BRL", "Brazilian Real"),
                Map.entry("BSD", "Bahamian Dollar"),
                Map.entry("BTN", "Bhutanese Ngultrum"),
                Map.entry("BWP", "Botswana Pula"),
                Map.entry("BYN", "Belarusian Ruble"),
                Map.entry("BZD", "Belize Dollar"),
                Map.entry("CAD", "Canadian Dollar"),
                Map.entry("CDF", "Congolese Franc"),
                Map.entry("CHF", "Swiss Franc"),
                Map.entry("CLP", "Chilean Peso"),
                Map.entry("CNY", "Chinese Yuan"),
                Map.entry("COP", "Colombian Peso"),
                Map.entry("CRC", "Costa Rican Colón"),
                Map.entry("CUP", "Cuban Peso"),
                Map.entry("CVE", "Cape Verdean Escudo"),
                Map.entry("CZK", "Czech Koruna"),
                Map.entry("DJF", "Djiboutian Franc"),
                Map.entry("DKK", "Danish Krone"),
                Map.entry("DOP", "Dominican Peso"),
                Map.entry("DZD", "Algerian Dinar"),
                Map.entry("EGP", "Egyptian Pound"),
                Map.entry("ERN", "Eritrean Nakfa"),
                Map.entry("ETB", "Ethiopian Birr"),
                Map.entry("EUR", "Euro"),
                Map.entry("FJD", "Fijian Dollar"),
                Map.entry("FKP", "Falkland Islands Pound"),
                Map.entry("FOK", "Faroese Króna"),
                Map.entry("GBP", "British Pound"),
                Map.entry("GEL", "Georgian Lari"),
                Map.entry("GGP", "Guernsey Pound"),
                Map.entry("GHS", "Ghanaian Cedi"),
                Map.entry("GIP", "Gibraltar Pound"),
                Map.entry("GMD", "Gambian Dalasi"),
                Map.entry("GNF", "Guinean Franc"),
                Map.entry("GTQ", "Guatemalan Quetzal"),
                Map.entry("GYD", "Guyanese Dollar"),
                Map.entry("HKD", "Hong Kong Dollar"),
                Map.entry("HNL", "Honduran Lempira"),
                Map.entry("HRK", "Croatian Kuna"),
                Map.entry("HTG", "Haitian Gourde"),
                Map.entry("HUF", "Hungarian Forint"),
                Map.entry("IDR", "Indonesian Rupiah"),
                Map.entry("ILS", "Israeli New Shekel"),
                Map.entry("IMP", "Isle of Man Pound"),
                Map.entry("INR", "Indian Rupee"),
                Map.entry("IQD", "Iraqi Dinar"),
                Map.entry("IRR", "Iranian Rial"),
                Map.entry("ISK", "Icelandic Króna"),
                Map.entry("JEP", "Jersey Pound"),
                Map.entry("JMD", "Jamaican Dollar"),
                Map.entry("JOD", "Jordanian Dinar"),
                Map.entry("JPY", "Japanese Yen"),
                Map.entry("KES", "Kenyan Shilling"),
                Map.entry("KGS", "Kyrgyzstani Som"),
                Map.entry("KHR", "Cambodian Riel"),
                Map.entry("KID", "Kiribati Dollar"),
                Map.entry("KMF", "Comorian Franc"),
                Map.entry("KRW", "South Korean Won"),
                Map.entry("KWD", "Kuwaiti Dinar"),
                Map.entry("KYD", "Cayman Islands Dollar"),
                Map.entry("KZT", "Kazakhstani Tenge"),
                Map.entry("LAK", "Lao Kip"),
                Map.entry("LBP", "Lebanese Pound"),
                Map.entry("LKR", "Sri Lankan Rupee"),
                Map.entry("LRD", "Liberian Dollar"),
                Map.entry("LSL", "Lesotho Loti"),
                Map.entry("LYD", "Libyan Dinar"),
                Map.entry("MAD", "Moroccan Dirham"),
                Map.entry("MDL", "Moldovan Leu"),
                Map.entry("MGA", "Malagasy Ariary"),
                Map.entry("MKD", "Macedonian Denar"),
                Map.entry("MMK", "Myanmar Kyat"),
                Map.entry("MNT", "Mongolian Tögrög"),
                Map.entry("MOP", "Macanese Pataca"),
                Map.entry("MRU", "Mauritanian Ouguiya"),
                Map.entry("MUR", "Mauritian Rupee"),
                Map.entry("MVR", "Maldivian Rufiyaa"),
                Map.entry("MWK", "Malawian Kwacha"),
                Map.entry("MXN", "Mexican Peso"),
                Map.entry("MYR", "Malaysian Ringgit"),
                Map.entry("MZN", "Mozambican Metical"),
                Map.entry("NAD", "Namibian Dollar"),
                Map.entry("NGN", "Nigerian Naira"),
                Map.entry("NIO", "Nicaraguan Córdoba"),
                Map.entry("NOK", "Norwegian Krone"),
                Map.entry("NPR", "Nepalese Rupee"),
                Map.entry("NZD", "New Zealand Dollar"),
                Map.entry("OMR", "Omani Rial"),
                Map.entry("PAB", "Panamanian Balboa"),
                Map.entry("PEN", "Peruvian Sol"),
                Map.entry("PGK", "Papua New Guinean Kina"),
                Map.entry("PHP", "Philippine Peso"),
                Map.entry("PKR", "Pakistani Rupee"),
                Map.entry("PLN", "Polish Zloty"),
                Map.entry("PYG", "Paraguayan Guaraní"),
                Map.entry("QAR", "Qatari Riyal"),
                Map.entry("RON", "Romanian Leu"),
                Map.entry("RSD", "Serbian Dinar"),
                Map.entry("RUB", "Russian Ruble"),
                Map.entry("RWF", "Rwandan Franc"),
                Map.entry("SAR", "Saudi Riyal"),
                Map.entry("SBD", "Solomon Islands Dollar"),
                Map.entry("SCR", "Seychellois Rupee"),
                Map.entry("SDG", "Sudanese Pound"),
                Map.entry("SEK", "Swedish Krona"),
                Map.entry("SGD", "Singapore Dollar"),
                Map.entry("SHP", "Saint Helena Pound"),
                Map.entry("SLE", "Sierra Leonean Leone"),
                Map.entry("SLL", "Sierra Leonean Leone"),
                Map.entry("SOS", "Somali Shilling"),
                Map.entry("SRD", "Surinamese Dollar"),
                Map.entry("SSP", "South Sudanese Pound"),
                Map.entry("STN", "São Tomé and Príncipe Dobra"),
                Map.entry("SYP", "Syrian Pound"),
                Map.entry("SZL", "Swazi Lilangeni"),
                Map.entry("THB", "Thai Baht"),
                Map.entry("TJS", "Tajikistani Somoni"),
                Map.entry("TMT", "Turkmenistani Manat"),
                Map.entry("TND", "Tunisian Dinar"),
                Map.entry("TOP", "Tongan Paʻanga"),
                Map.entry("TRY", "Turkish Lira"),
                Map.entry("TTD", "Trinidad and Tobago Dollar"),
                Map.entry("TVD", "Tuvaluan Dollar"),
                Map.entry("TWD", "New Taiwan Dollar"),
                Map.entry("TZS", "Tanzanian Shilling"),
                Map.entry("UAH", "Ukrainian Hryvnia"),
                Map.entry("UGX", "Ugandan Shilling"),
                Map.entry("UYU", "Uruguayan Peso"),
                Map.entry("UZS", "Uzbekistani Som"),
                Map.entry("VES", "Venezuelan Bolívar Soberano"),
                Map.entry("VND", "Vietnamese Dong"),
                Map.entry("VUV", "Vanuatu Vatu"),
                Map.entry("WST", "Samoan Tālā"),
                Map.entry("XAF", "Central African CFA Franc"),
                Map.entry("XCD", "East Caribbean Dollar"),
                Map.entry("XDR", "Special Drawing Rights"),
                Map.entry("XOF", "West African CFA Franc"),
                Map.entry("XPF", "CFP Franc"),
                Map.entry("YER", "Yemeni Rial"),
                Map.entry("ZAR", "South African Rand"),
                Map.entry("ZMW", "Zambian Kwacha"),
                Map.entry("ZWL", "Zimbabwean Dollar")
        );
        return currencyNames.getOrDefault(code, "Unknown Currency");
    }
}

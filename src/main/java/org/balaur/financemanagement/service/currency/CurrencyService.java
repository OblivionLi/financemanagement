package org.balaur.financemanagement.service.currency;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.balaur.financemanagement.model.currency.Currency;
import org.balaur.financemanagement.repository.CurrencyRepository;
import org.balaur.financemanagement.response.CurrencyResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class CurrencyService {
    private final CurrencyRepository currencyRepository;

    @PostConstruct
    public void initCurrencies() {
        fetchAndSaveCurrencies();
    }

    public void fetchAndSaveCurrencies() {
        String url = "https://api.exchangerate-api.com/v4/latest/EUR";

        RestTemplate restTemplate = new RestTemplate();
        Map<String, Object> response = restTemplate.getForObject(url, Map.class);

        if (response != null && response.containsKey("rates")) {
            Map<String, Number> rates = (Map<String, Number>) response.get("rates");
            long timeLastUpdated = ((Number) response.get("time_last_updated")).longValue();
            LocalDateTime lastUpdatedDateTime = Instant.ofEpochSecond(timeLastUpdated).atZone(ZoneId.systemDefault()).toLocalDateTime();

            for (Map.Entry<String, Number> entry : rates.entrySet()) {
                String code = entry.getKey();
                BigDecimal rate = BigDecimal.valueOf(entry.getValue().doubleValue());
                String name = getCurrencyName(code);

                Currency currency = currencyRepository.findByCode(code)
                        .orElse(new Currency());

                currency.setCode(code);
                currency.setName(name);
                currency.setRate(rate);
                currency.setLastTimeUpdated(lastUpdatedDateTime);
                currencyRepository.save(currency);
            }
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

    public ResponseEntity<List<CurrencyResponse>> getCurrencies() {
        List<Currency> currencyList = currencyRepository.findAll();
        List<CurrencyResponse> currencyResponseList = new ArrayList<>();

        for (Currency currency : currencyList) {
            CurrencyResponse currencyResponse = CurrencyResponse.builder()
                    .id(currency.getId())
                    .name(currency.getName())
                    .code(currency.getCode())
                    .lastTimeUpdated(currency.getLastTimeUpdated())
                    .rate(currency.getRate())
                    .build();

            currencyResponseList.add(currencyResponse);
        }

        return ResponseEntity.ok(currencyResponseList);
    }
}

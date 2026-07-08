package com.example.expense.service;

import com.example.expense.model.VendorCategoryRule;
import com.example.expense.repository.VendorCategoryRuleRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class CategorizationService {

    private final VendorCategoryRuleRepository ruleRepo;

    private static final Map<String, String> DEFAULT_RULES = Map.ofEntries(
            Map.entry("swiggy", "Food"),
            Map.entry("zomato", "Food"),
            Map.entry("dominos", "Food"),
            Map.entry("mcdonald", "Food"),
            Map.entry("starbucks", "Food"),
            Map.entry("uber", "Transport"),
            Map.entry("ola", "Transport"),
            Map.entry("rapido", "Transport"),
            Map.entry("indianoil", "Transport"),
            Map.entry("shell", "Transport"),
            Map.entry("amazon", "Shopping"),
            Map.entry("apple", "Shopping"),
            Map.entry("flipkart", "Shopping"),
            Map.entry("myntra", "Shopping"),
            Map.entry("ajio", "Shopping"),
            Map.entry("netflix", "Entertainment"),
            Map.entry("spotify", "Entertainment"),
            Map.entry("hotstar", "Entertainment"),
            Map.entry("bookmyshow", "Entertainment"),
            Map.entry("airtel", "Utilities"),
            Map.entry("jio", "Utilities"),
            Map.entry("vodafone", "Utilities"),
            Map.entry("electricity", "Utilities"),
            Map.entry("bescom", "Utilities"),
            Map.entry("apollo", "Health"),
            Map.entry("pharmeasy", "Health"),
            Map.entry("1mg", "Health")
    );

    @PostConstruct
    public void seed() {
        if (ruleRepo.count() == 0) {
            List<VendorCategoryRule> rules = DEFAULT_RULES.entrySet().stream()
                    .map(e -> VendorCategoryRule.builder()
                            .vendorKeyword(e.getKey())
                            .category(e.getValue())
                            .build())
                    .toList();
            ruleRepo.saveAll(rules);
        }
    }

    public String categorize(String vendor) {
        if (vendor == null || vendor.isBlank()) return "Uncategorized";
        String lower = vendor.toLowerCase();
        return ruleRepo.findAll().stream()
                .filter(r -> lower.contains(r.getVendorKeyword().toLowerCase()))
                .map(VendorCategoryRule::getCategory)
                .findFirst()
                .orElse("Uncategorized");
    }
}

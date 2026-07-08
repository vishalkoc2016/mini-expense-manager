package com.example.expense.controller;

import com.example.expense.model.VendorCategoryRule;
import com.example.expense.repository.VendorCategoryRuleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/rules")
@RequiredArgsConstructor
public class RuleController {

    private final VendorCategoryRuleRepository repo;

    @GetMapping
    public List<VendorCategoryRule> list() {
        return repo.findAll();
    }

    @PostMapping
    public VendorCategoryRule create(@RequestBody VendorCategoryRule rule) {
        rule.setId(null);
        rule.setVendorKeyword(rule.getVendorKeyword().toLowerCase());
        return repo.save(rule);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        repo.deleteById(id);
    }
}

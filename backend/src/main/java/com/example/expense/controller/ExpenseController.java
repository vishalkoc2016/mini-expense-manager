package com.example.expense.controller;

import com.example.expense.dto.CsvUploadResponse;
import com.example.expense.dto.DashboardResponse;
import com.example.expense.dto.ExpenseRequest;
import com.example.expense.model.Expense;
import com.example.expense.service.ExpenseService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/expenses")
@RequiredArgsConstructor
public class ExpenseController {

    private final ExpenseService service;

    @GetMapping
    public List<Expense> list() {
        return service.list();
    }

    @PostMapping
    public ResponseEntity<Expense> create(@Valid @RequestBody ExpenseRequest req) {
        return ResponseEntity.ok(service.create(req));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/upload")
    public ResponseEntity<CsvUploadResponse> upload(@RequestParam("file") MultipartFile file) throws IOException {
        return ResponseEntity.ok(service.importCsv(file));
    }

    @GetMapping("/dashboard")
    public DashboardResponse dashboard() {
        return service.dashboard();
    }
}

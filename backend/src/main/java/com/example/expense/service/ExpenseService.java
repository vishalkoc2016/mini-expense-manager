package com.example.expense.service;

import com.example.expense.dto.CsvUploadResponse;
import com.example.expense.dto.DashboardResponse;
import com.example.expense.dto.ExpenseRequest;
import com.example.expense.model.Expense;
import com.example.expense.repository.ExpenseRepository;
import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ExpenseService {

    private final ExpenseRepository expenseRepo;
    private final CategorizationService categorization;
    private final AnomalyService anomalyService;

    private static final List<DateTimeFormatter> DATE_FORMATS = List.of(
            DateTimeFormatter.ISO_LOCAL_DATE,
            DateTimeFormatter.ofPattern("dd/MM/yyyy"),
            DateTimeFormatter.ofPattern("MM/dd/yyyy"),
            DateTimeFormatter.ofPattern("dd-MM-yyyy")
    );

    @Transactional
    public Expense create(ExpenseRequest req) {
        String category = categorization.categorize(req.getVendor());
        boolean anomaly = anomalyService.isAnomaly(req.getAmount(), category);
        Expense e = Expense.builder()
                .date(req.getDate())
                .amount(req.getAmount())
                .vendor(req.getVendor())
                .description(req.getDescription())
                .category(category)
                .anomaly(anomaly)
                .build();
        Expense saved = expenseRepo.save(e);
        anomalyService.recomputeForCategory(category);
        return expenseRepo.findById(saved.getId()).orElse(saved);
    }

    public List<Expense> list() {
        return expenseRepo.findAll(org.springframework.data.domain.Sort.by(
                org.springframework.data.domain.Sort.Direction.DESC, "date", "id"));
    }

    public void delete(Long id) {
        expenseRepo.deleteById(id);
    }

    @Transactional
    public CsvUploadResponse importCsv(MultipartFile file) throws IOException {
        int inserted = 0;
        int failed = 0;
        List<String> errors = new ArrayList<>();
        Set<String> touchedCategories = new HashSet<>();

        try (CSVReader reader = new CSVReader(new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8))) {
            String[] header = reader.readNext();
            if (header == null) {
                return new CsvUploadResponse(0, 0, List.of("Empty CSV"));
            }
            Map<String, Integer> idx = indexHeader(header);
            requireColumn(idx, "date");
            requireColumn(idx, "amount");
            requireColumn(idx, "vendor");

            String[] row;
            int lineNum = 1;
            while ((row = reader.readNext()) != null) {
                lineNum++;
                try {
                    LocalDate date = parseDate(get(row, idx, "date"));
                    BigDecimal amount = new BigDecimal(get(row, idx, "amount").trim());
                    String vendor = get(row, idx, "vendor").trim();
                    String description = idx.containsKey("description") ? get(row, idx, "description") : null;
                    if (vendor.isEmpty()) throw new IllegalArgumentException("vendor required");

                    String category = categorization.categorize(vendor);
                    Expense e = Expense.builder()
                            .date(date)
                            .amount(amount)
                            .vendor(vendor)
                            .description(description)
                            .category(category)
                            .anomaly(false)
                            .build();
                    expenseRepo.save(e);
                    touchedCategories.add(category);
                    inserted++;
                } catch (Exception ex) {
                    failed++;
                    errors.add("Line " + lineNum + ": " + ex.getMessage());
                }
            }
        } catch (CsvValidationException e) {
            throw new IOException("CSV parse error: " + e.getMessage(), e);
        }

        touchedCategories.forEach(anomalyService::recomputeForCategory);
        return new CsvUploadResponse(inserted, failed, errors);
    }

    public DashboardResponse dashboard() {
        List<Expense> all = expenseRepo.findAll();
        YearMonth currentMonth = YearMonth.now();

        Map<String, BigDecimal> monthly = all.stream()
                .filter(e -> YearMonth.from(e.getDate()).equals(currentMonth))
                .collect(Collectors.groupingBy(
                        Expense::getCategory,
                        Collectors.reducing(BigDecimal.ZERO, Expense::getAmount, BigDecimal::add)));

        List<DashboardResponse.VendorTotal> topVendors = all.stream()
                .collect(Collectors.groupingBy(
                        Expense::getVendor,
                        Collectors.reducing(BigDecimal.ZERO, Expense::getAmount, BigDecimal::add)))
                .entrySet().stream()
                .sorted(Map.Entry.<String, BigDecimal>comparingByValue().reversed())
                .limit(5)
                .map(e -> new DashboardResponse.VendorTotal(e.getKey(), e.getValue()))
                .toList();

        List<Long> anomalyIds = all.stream().filter(Expense::isAnomaly).map(Expense::getId).toList();

        return new DashboardResponse(monthly, topVendors, anomalyIds.size(), anomalyIds);
    }

    private Map<String, Integer> indexHeader(String[] header) {
        Map<String, Integer> idx = new HashMap<>();
        for (int i = 0; i < header.length; i++) {
            idx.put(header[i].trim().toLowerCase(), i);
        }
        return idx;
    }

    private String get(String[] row, Map<String, Integer> idx, String col) {
        Integer i = idx.get(col);
        if (i == null || i >= row.length) return "";
        return row[i] == null ? "" : row[i];
    }

    private void requireColumn(Map<String, Integer> idx, String col) {
        if (!idx.containsKey(col)) {
            throw new IllegalArgumentException("Missing required column: " + col);
        }
    }

    private LocalDate parseDate(String raw) {
        String s = raw == null ? "" : raw.trim();
        for (DateTimeFormatter fmt : DATE_FORMATS) {
            try {
                return LocalDate.parse(s, fmt);
            } catch (Exception ignored) {
            }
        }
        throw new IllegalArgumentException("Unrecognized date format: " + s);
    }
}

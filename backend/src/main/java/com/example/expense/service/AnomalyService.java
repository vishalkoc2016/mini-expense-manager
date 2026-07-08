package com.example.expense.service;

import com.example.expense.model.Expense;
import com.example.expense.repository.ExpenseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AnomalyService {

    private static final BigDecimal THRESHOLD_MULTIPLIER = BigDecimal.valueOf(3);

    private final ExpenseRepository expenseRepo;

    public boolean isAnomaly(BigDecimal amount, String category) {
        BigDecimal avg = expenseRepo.averageForCategory(category);
        if (avg == null || avg.signum() == 0) return false;
        return amount.compareTo(avg.multiply(THRESHOLD_MULTIPLIER)) > 0;
    }

    /**
     * Recomputes anomaly flags for the given category, useful after bulk inserts
     * where a single item's average would otherwise be skewed by its own value.
     */
    @Transactional
    public void recomputeForCategory(String category) {
        BigDecimal avg = expenseRepo.averageForCategory(category);
        if (avg == null || avg.signum() == 0) return;
        BigDecimal threshold = avg.multiply(THRESHOLD_MULTIPLIER);
        List<Expense> items = expenseRepo.findByCategory(category);
        for (Expense e : items) {
            e.setAnomaly(e.getAmount().compareTo(threshold) > 0);
        }
        expenseRepo.saveAll(items);
    }
}

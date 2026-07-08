package com.example.expense.repository;

import com.example.expense.model.Expense;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.math.BigDecimal;
import java.util.List;

public interface ExpenseRepository extends JpaRepository<Expense, Long> {

    List<Expense> findByCategory(String category);

    List<Expense> findByAnomalyTrue();

    @Query("select coalesce(avg(e.amount), 0) from Expense e where e.category = :category")
    BigDecimal averageForCategory(String category);
}

package com.example.expense.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class ExpenseRequest {
    @NotNull
    private LocalDate date;

    @NotNull
    @Positive
    private BigDecimal amount;

    @NotNull
    @Size(min = 1, max = 128)
    private String vendor;

    @Size(max = 512)
    private String description;
}

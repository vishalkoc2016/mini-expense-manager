package com.example.expense.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class CsvUploadResponse {
    private int inserted;
    private int failed;
    private List<String> errors;
}

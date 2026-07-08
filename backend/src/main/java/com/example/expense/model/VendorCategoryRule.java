package com.example.expense.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "vendor_category_rules")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VendorCategoryRule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 128)
    private String vendorKeyword;

    @Column(nullable = false, length = 64)
    private String category;
}

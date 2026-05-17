package com.comedor.backend.infrastructure.adapters.out.persistence.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "menu_report")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MenuReportEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "menu_report_id")
    private int id;

    @Column(nullable = false,unique = true)
    private LocalDate date;

    @ElementCollection
    @Column(name = "user_id")
    private List<Integer> cooks;

    @Column(nullable = false, length = 100)
    private String menu;

    @OneToMany(mappedBy = "report")
    private List<RecordEntity> productRecord = new ArrayList<>();

    @OneToMany(mappedBy = "report")
    private List<BeneficiaryControlEntity> beneficiariosRecord = new ArrayList<>();

    @Column(nullable = false,precision = 10, scale = 2)
    private BigDecimal totalEarned = BigDecimal.ZERO;

    @Column(nullable = false,precision = 10, scale = 2)
    private BigDecimal totalSpent = BigDecimal.ZERO;
}
package com.comedor.backend.infrastructure.adapters.out.persistence.entity;

import com.comedor.backend.domain.model.enums.StatusMenuReport;
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
    private Integer id;

    @Column(nullable = false)
    private LocalDate date;

    @ElementCollection
    @CollectionTable(
            name = "menu_report_cooks",
            joinColumns = @JoinColumn(name = "menu_report_id")
    )
    @Column(name = "cook_id")
    private List<Integer> cooks = new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "dish_menu_id", nullable = false)
    private DishMenuEntity dishMenu;

    @Column(nullable = false)
    private Integer quantityPrepared = 0;

    @Column(nullable = false)
    private Integer quantityRemaining = 0;

    @OneToMany(mappedBy = "report", cascade = CascadeType.ALL,orphanRemoval = true)
    private List<BeneficiaryControlEntity> beneficiaryControls = new ArrayList<>();

    @OneToMany(mappedBy = "menuReport", cascade = CascadeType.ALL,orphanRemoval = true)
    private List<StockMovementEntity> stockMovements = new ArrayList<>();

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal totalEarned = BigDecimal.ZERO;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal totalSpent = BigDecimal.ZERO;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatusMenuReport status;
}
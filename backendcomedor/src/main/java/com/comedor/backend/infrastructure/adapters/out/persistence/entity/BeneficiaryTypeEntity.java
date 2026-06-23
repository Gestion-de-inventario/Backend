package com.comedor.backend.infrastructure.adapters.out.persistence.entity;

import com.comedor.backend.domain.model.enums.Status;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Entity
@Table(name = "beneficiary_type")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BeneficiaryTypeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, length = 70)
    private String name;


    @Column(length = 255)
    private String description;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal menu_cost;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Status status = Status.ACTIVO;
}





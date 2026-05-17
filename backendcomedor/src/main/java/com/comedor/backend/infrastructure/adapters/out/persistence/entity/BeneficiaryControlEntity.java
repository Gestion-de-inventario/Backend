package com.comedor.backend.infrastructure.adapters.out.persistence.entity;

import com.comedor.backend.domain.model.enums.MetodoPago;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Entity
@Table(name = "beneficiary_control")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BeneficiaryControlEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "beneficiary_control_id")
    private int id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "beneficiary_id", nullable = false)
    private BeneficiaryEntity beneficiary;

    @Column(nullable = false)
    private boolean received = false;

    @Column(nullable = false)
    private boolean paid = false;

    @Enumerated(EnumType.STRING)
    private MetodoPago payMethod;

    @Column(name = "menus_amount", nullable = false)
    private int menusAmount;

    @Column(name = "menu_price",nullable = false ,precision = 10, scale = 2)
    private BigDecimal menuPrice;

    @ManyToOne
    @JoinColumn(name = "menu_report_id")
    private MenuReportEntity report;

    public boolean getIsReceived() {
        return received;
    }

    public boolean getIsPaid() {
        return paid;
    }
}
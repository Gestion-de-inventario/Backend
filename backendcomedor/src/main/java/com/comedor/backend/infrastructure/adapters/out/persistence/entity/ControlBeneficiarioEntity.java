package com.comedor.backend.infrastructure.adapters.out.persistence.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Entity
@Table(name = "control_beneficiario")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ControlBeneficiarioEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "control_beneficiario_id")
    private int id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "beneficiario_id", nullable = false)
    private BeneficiarioEntity beneficiario;

    @Column(nullable = false)
    private boolean received;

    @Column(name = "menus_amount")
    private Integer menusAmount;

    @Column(name = "menu_price", precision = 10, scale = 2)
    private BigDecimal menuPrice;

    @ManyToOne
    @JoinColumn(name = "reporte_menu_id")
    private ReporteMenuEntity reporte;

}
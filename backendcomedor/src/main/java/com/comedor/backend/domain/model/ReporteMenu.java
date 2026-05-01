package com.comedor.backend.domain.model;

import java.math.BigDecimal;
import java.time.LocalDate;

public class ReporteMenu {
    private int id;
    private LocalDate date;
    private Usuario[] cooks;
    private String menu;
    private Registro[] product_record;
    private ControlBeneficiario[] beneficiarios_record;
    private BigDecimal total_earned;
    private BigDecimal total_spent;
}
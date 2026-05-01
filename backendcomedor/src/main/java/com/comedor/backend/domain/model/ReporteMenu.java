package com.comedor.backend.domain.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public class ReporteMenu {
    private int id;
    private LocalDate date;
    private List<Integer> cooks;
    private String menu;
    private List<Registro> productRecord;
    private List<ControlBeneficiario> beneficiariosRecord;
    private BigDecimal totalEarned;
    private BigDecimal totalSpent;
}
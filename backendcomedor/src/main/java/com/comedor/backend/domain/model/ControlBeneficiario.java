package com.comedor.backend.domain.model;

import java.math.BigDecimal;

public class ControlBeneficiario {
    private int id;
    private Beneficiario beneficiario;
    private boolean received;
    private Integer menus_amount;
    private BigDecimal menu_price;
    private BigDecimal total_earned;
}
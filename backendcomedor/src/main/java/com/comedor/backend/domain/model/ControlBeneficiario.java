package com.comedor.backend.domain.model;

import java.math.BigDecimal;

public class ControlBeneficiario {
    private int id;
    private Beneficiario beneficiario;
    private boolean received;
    private int menusAmount;
    private BigDecimal menuPrice;
}
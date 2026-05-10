package com.comedor.backend.infrastructure.adapters.in.web.dto.response;

import com.comedor.backend.domain.model.enums.MetodoPago;

import java.math.BigDecimal;

public class RegistroBeneficiarioResponseDTO {
    private String name;
    private String lastName;
    private BigDecimal cantidad;
    private BigDecimal total;
    private MetodoPago metodoPago;
    private boolean pago;
    private boolean entregado;
}

package com.comedor.backend.infrastructure.adapters.in.web.dto.response;

import com.comedor.backend.domain.model.enums.MetodoPago;
import lombok.Data;

import java.math.BigDecimal;
@Data
public class RegistroBeneficiarioResponseDTO {
    private String name;
    private String lastName;
    private int cantidad;
    private BigDecimal total;
    private MetodoPago metodoPago;
    private boolean pago;
    private boolean entregado;
}

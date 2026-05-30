package com.comedor.backend.infrastructure.adapters.in.web.dto.response;

import com.comedor.backend.domain.model.enums.MetodoPago;
import lombok.Data;

import java.math.BigDecimal;
@Data
public class ResumenReporteMenuResponseDTO {
    private BigDecimal totalEarned;
    private BigDecimal totalSpent;
    private BigDecimal neto;
    private int beneficiariosCount;
    private MetodoPago mostUsedPaymentMethod;
}

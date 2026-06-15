package com.comedor.backend.infrastructure.adapters.in.web.dto.response;

import com.comedor.backend.domain.model.enums.MetodoPago;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;
@Data
public class ListMenuReportDetailResponseDTO {
    List<DetalleReporteMenuResponseDTO> reports;
    private BigDecimal totalEarned;
    private BigDecimal totalSpent;
    private BigDecimal net;
    private int uniqueBeneficiaryCount;
    private MetodoPago mostUsedPaymentMethod;
}

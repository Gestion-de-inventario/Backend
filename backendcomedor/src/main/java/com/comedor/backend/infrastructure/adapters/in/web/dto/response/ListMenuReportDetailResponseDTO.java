package com.comedor.backend.infrastructure.adapters.in.web.dto.response;

import com.comedor.backend.domain.model.enums.PaymentMethod;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;
@Data
public class ListMenuReportDetailResponseDTO {
    List<MenuReportDetailResponseDTO> reports;
    private BigDecimal totalEarned;
    private BigDecimal totalSpent;
    private BigDecimal net;
    private int uniqueBeneficiaryCount;
    private PaymentMethod mostUsedPaymentMethod;
}

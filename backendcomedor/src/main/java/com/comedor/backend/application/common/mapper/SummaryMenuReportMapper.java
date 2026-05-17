package com.comedor.backend.application.common.mapper;

import com.comedor.backend.domain.model.MenuReport;
import com.comedor.backend.domain.model.enums.MetodoPago;
import com.comedor.backend.infrastructure.adapters.in.web.dto.response.ResumenReporteMenuResponseDTO;
import org.springframework.stereotype.Component;

@Component
public class SummaryMenuReportMapper {

    public ResumenReporteMenuResponseDTO toDto(
            MenuReport reporte,
            int beneficiariosCount,
            MetodoPago metodoMasUsado
    ) {

        ResumenReporteMenuResponseDTO dto =
                new ResumenReporteMenuResponseDTO();

        dto.setTotalEarned(
                reporte.getTotalEarned()
        );

        dto.setTotalSpent(
                reporte.getTotalSpent()
        );

        dto.setNeto(
                reporte.getTotalEarned()
                        .subtract(reporte.getTotalSpent())
        );

        dto.setBeneficiariosCount(
                beneficiariosCount
        );

        dto.setMostUsedPaymentMethod(
                metodoMasUsado
        );

        return dto;
    }
}
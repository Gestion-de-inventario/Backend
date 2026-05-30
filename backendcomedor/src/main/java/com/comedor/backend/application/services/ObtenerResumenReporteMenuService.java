package com.comedor.backend.application.services;

import com.comedor.backend.application.common.mapper.SummaryMenuReportMapper;
import com.comedor.backend.application.ports.in.ObtenerResumenReporteMenuUseCase;
import com.comedor.backend.application.ports.out.BeneficiaryControlRepositoryPort;
import com.comedor.backend.application.ports.out.MenuReportRepositoryPort;
import com.comedor.backend.domain.model.BeneficiaryControl;
import com.comedor.backend.domain.model.MenuReport;
import com.comedor.backend.domain.model.enums.MetodoPago;
import com.comedor.backend.infrastructure.adapters.in.web.dto.response.ResumenReporteMenuResponseDTO;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

public class ObtenerResumenReporteMenuService implements ObtenerResumenReporteMenuUseCase {
    private final MenuReportRepositoryPort menuReportRepositoryPort;
    private final BeneficiaryControlRepositoryPort beneficiaryControlRepositoryPort;
    private final SummaryMenuReportMapper summaryMenuReportMapper;
    public ObtenerResumenReporteMenuService(
            MenuReportRepositoryPort menuReportRepositoryPort,
            BeneficiaryControlRepositoryPort beneficiaryControlRepositoryPort,
            SummaryMenuReportMapper summaryMenuReportMapper
    ) {
        this.menuReportRepositoryPort = menuReportRepositoryPort;
        this.beneficiaryControlRepositoryPort = beneficiaryControlRepositoryPort;
        this.summaryMenuReportMapper = summaryMenuReportMapper;
    }
    @Transactional(readOnly = true)
    @Override
    public ResumenReporteMenuResponseDTO obtenerResumen(int id) {

        MenuReport reporte =
                menuReportRepositoryPort.findById(id);

        List<BeneficiaryControl> beneficiarios =
                beneficiaryControlRepositoryPort
                        .findByReporteId(id);

        MetodoPago metodoMasUsado =
                beneficiarios.stream()
                        .filter(BeneficiaryControl::getPaid)
                        .map(BeneficiaryControl::getPayMethod)
                        .filter(Objects::nonNull)
                        .collect(Collectors.groupingBy(
                                metodo -> metodo,
                                Collectors.counting()
                        ))
                        .entrySet()
                        .stream()
                        .max(Map.Entry.comparingByValue())
                        .map(Map.Entry::getKey)
                        .orElse(null);

        return summaryMenuReportMapper.toDto(
                reporte,
                beneficiarios.size(),
                metodoMasUsado
        );
    }
}

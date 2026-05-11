package com.comedor.backend.application.services;

import com.comedor.backend.application.common.mapper.ResumenReporteMenuMapper;
import com.comedor.backend.application.ports.in.ObtenerResumenReporteMenuUseCase;
import com.comedor.backend.application.ports.out.ControlBeneficiarioRepositoryPort;
import com.comedor.backend.application.ports.out.ReporteMenuRepositoryPort;
import com.comedor.backend.domain.model.ControlBeneficiario;
import com.comedor.backend.domain.model.ReporteMenu;
import com.comedor.backend.domain.model.enums.MetodoPago;
import com.comedor.backend.infrastructure.adapters.in.web.dto.response.ResumenReporteMenuResponseDTO;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

public class ObtenerResumenReporteMenuService implements ObtenerResumenReporteMenuUseCase {
    private final ReporteMenuRepositoryPort reporteMenuRepositoryPort;
    private final ControlBeneficiarioRepositoryPort controlBeneficiarioRepositoryPort;
    private final ResumenReporteMenuMapper resumenReporteMenuMapper;
    public ObtenerResumenReporteMenuService(
            ReporteMenuRepositoryPort reporteMenuRepositoryPort,
            ControlBeneficiarioRepositoryPort controlBeneficiarioRepositoryPort,
            ResumenReporteMenuMapper resumenReporteMenuMapper
    ) {
        this.reporteMenuRepositoryPort = reporteMenuRepositoryPort;
        this.controlBeneficiarioRepositoryPort = controlBeneficiarioRepositoryPort;
        this.resumenReporteMenuMapper = resumenReporteMenuMapper;
    }
    @Transactional(readOnly = true)
    @Override
    public ResumenReporteMenuResponseDTO obtenerResumen(int id) {

        ReporteMenu reporte =
                reporteMenuRepositoryPort.findById(id);

        List<ControlBeneficiario> beneficiarios =
                controlBeneficiarioRepositoryPort
                        .findByReporteId(id);

        MetodoPago metodoMasUsado =
                beneficiarios.stream()
                        .filter(ControlBeneficiario::getPaid)
                        .map(ControlBeneficiario::getPayMethod)
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

        return resumenReporteMenuMapper.toDto(
                reporte,
                beneficiarios.size(),
                metodoMasUsado
        );
    }
}

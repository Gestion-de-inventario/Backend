package com.comedor.backend.application.services;

import com.comedor.backend.application.common.mapper.MenuReportMapper;
import com.comedor.backend.application.ports.in.CrearReporteMenuUseCase;
import com.comedor.backend.application.ports.out.MenuReportRepositoryPort;
import com.comedor.backend.domain.exceptions.ReporteMenuYaExistente;
import com.comedor.backend.domain.model.MenuReport;
import com.comedor.backend.infrastructure.adapters.in.web.dto.request.ReporteMenuRequestDTO;
import com.comedor.backend.infrastructure.adapters.in.web.dto.response.ReporteMenuResponseDTO;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;


public class CrearReporteMenuService implements CrearReporteMenuUseCase{
    private final MenuReportRepositoryPort repository;
    private final MenuReportMapper mapper;
    public CrearReporteMenuService(MenuReportRepositoryPort repository, MenuReportMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    @Override
    public ReporteMenuResponseDTO crearReporteMenu(ReporteMenuRequestDTO reporteMenuRequestDTO) {

        if (repository.existByDate(LocalDate.now()))
        {
            throw new ReporteMenuYaExistente("Ya existe un reporte menu para hoy");
        }

        MenuReport reporte = new MenuReport();

        reporte.setDate(LocalDate.now());
        reporte.setCooks(reporteMenuRequestDTO.getCooks());
        reporte.setMenu(reporteMenuRequestDTO.getMenu());

        reporte.setBeneficiariosRecord(new ArrayList<>());
        reporte.setProductRecord(new ArrayList<>());

        reporte.setTotalEarned(BigDecimal.ZERO);
        reporte.setTotalSpent(BigDecimal.ZERO);
        return mapper.toDto(repository.create(reporte));
    }
}

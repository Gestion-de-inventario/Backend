package com.comedor.backend.application.services;

import com.comedor.backend.application.common.mapper.ReporteMenuMapper;
import com.comedor.backend.application.ports.in.CrearReporteMenuUseCase;
import com.comedor.backend.application.ports.out.ReporteMenuRepositoryPort;
import com.comedor.backend.domain.exceptions.ReporteMenuYaExistente;
import com.comedor.backend.domain.model.ReporteMenu;
import com.comedor.backend.infrastructure.adapters.in.web.dto.request.ReporteMenuRequestDTO;
import com.comedor.backend.infrastructure.adapters.in.web.dto.response.ReporteMenuResponseDTO;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;


public class CrearReporteMenuService implements CrearReporteMenuUseCase{
    private final ReporteMenuRepositoryPort repository;
    private final ReporteMenuMapper mapper;
    public CrearReporteMenuService(ReporteMenuRepositoryPort repository, ReporteMenuMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    @Override
    public ReporteMenuResponseDTO crearReporteMenu(ReporteMenuRequestDTO reporteMenuRequestDTO) {

        if (repository.existByDate(LocalDate.now()))
        {
            throw new ReporteMenuYaExistente("Ya existe un reporte menu para hoy");
        }

        ReporteMenu reporte = new ReporteMenu();

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

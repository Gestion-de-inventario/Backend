package com.comedor.backend.infrastructure.adapters.out.persistence;

import com.comedor.backend.application.ports.out.MenuReportRepositoryPort;
import com.comedor.backend.domain.exceptions.ReporteMenuNoEncontradoException;
import com.comedor.backend.domain.model.MenuReport;
import com.comedor.backend.infrastructure.adapters.out.persistence.entity.MenuReportEntity;
import com.comedor.backend.infrastructure.adapters.out.persistence.mapper.MenuReportEntityMapper;
import com.comedor.backend.infrastructure.adapters.out.persistence.repository.MenuReportJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

@Component
@RequiredArgsConstructor
public class MenuReportRepositoryAdapter implements MenuReportRepositoryPort {
    private final MenuReportJpaRepository menuReportJpaRepository;
    private final MenuReportEntityMapper menuReportEntityMapper;
    @Override
    public boolean existByDate(LocalDate date) {
        return menuReportJpaRepository.existsByDate(date);
    }

    @Override
    public MenuReport findByDate(LocalDate date) {
        return menuReportEntityMapper.toDomain(menuReportJpaRepository.findByDate(date));
    }

    @Override
    public MenuReport create(MenuReport menuReport) {

        MenuReportEntity entity =
                menuReportEntityMapper.toEntity(menuReport);

        return menuReportEntityMapper.toDomain(
                menuReportJpaRepository.save(entity)
        );
    }

    @Override
    public MenuReport update(MenuReport menuReport) {
        MenuReportEntity entity =
                menuReportEntityMapper
                        .toEntity(menuReport);

        MenuReportEntity updated =
                menuReportJpaRepository
                        .save(entity);

        return menuReportEntityMapper
                .toDomain(updated);
    }

    @Override
    public List<MenuReport> findByTimePeriod(LocalDate start, LocalDate end) {
        return List.of();
    }

    @Override
    public MenuReport findById(int id) {
        MenuReportEntity entity =
                menuReportJpaRepository
                        .findById(id)
                        .orElseThrow(() ->
                                new ReporteMenuNoEncontradoException(
                                        "No existe el reporte con id: " + id
                                )
                        );

        return menuReportEntityMapper
                .toDomain(entity);
    }
}

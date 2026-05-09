package com.comedor.backend.infrastructure.adapters.out.persistence;

import com.comedor.backend.application.ports.out.ReporteMenuRepositoryPort;
import com.comedor.backend.domain.model.ReporteMenu;
import com.comedor.backend.infrastructure.adapters.out.persistence.entity.ReporteMenuEntity;
import com.comedor.backend.infrastructure.adapters.out.persistence.mapper.ReporteMenuEntityMapper;
import com.comedor.backend.infrastructure.adapters.out.persistence.repository.ReporteMenuJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Component
@RequiredArgsConstructor
public class ReporteMenuRepositoryAdapter implements ReporteMenuRepositoryPort {
    private final ReporteMenuJpaRepository reporteMenuJpaRepository;
    private final ReporteMenuEntityMapper reporteMenuEntityMapper;
    @Override
    public boolean existByDate(Date date) {
        return reporteMenuJpaRepository.existsByDate(date);
    }

    @Override
    public ReporteMenu findByDate(Date date) {
        return reporteMenuEntityMapper.toDomain(reporteMenuJpaRepository.findByDate(date));
    }

    @Override
    public ReporteMenu create(ReporteMenu reporteMenu) {

        ReporteMenuEntity entity =
                reporteMenuEntityMapper.toEntity(reporteMenu);

        return reporteMenuEntityMapper.toDomain(
                reporteMenuJpaRepository.save(entity)
        );
    }

    @Override
    public ReporteMenu update(ReporteMenu reporteMenu) {
        return null;
    }

    @Override
    public List<ReporteMenu> findByTimePeriod(Date start, Date end) {
        return List.of();
    }
}

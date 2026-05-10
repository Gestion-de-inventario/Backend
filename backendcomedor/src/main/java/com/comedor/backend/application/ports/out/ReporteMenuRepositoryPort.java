package com.comedor.backend.application.ports.out;

import com.comedor.backend.domain.model.ReporteMenu;

import java.time.LocalDate;
import java.util.List;

public interface ReporteMenuRepositoryPort {
    boolean existByDate(LocalDate date);
    ReporteMenu findByDate(LocalDate date);
    ReporteMenu create(ReporteMenu reporteMenu);
    ReporteMenu update(ReporteMenu reporteMenu);
    List<ReporteMenu> findByTimePeriod(LocalDate start, LocalDate end);
}

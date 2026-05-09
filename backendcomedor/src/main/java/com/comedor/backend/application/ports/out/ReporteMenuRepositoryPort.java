package com.comedor.backend.application.ports.out;

import com.comedor.backend.domain.model.ReporteMenu;

import java.util.Date;
import java.util.List;

public interface ReporteMenuRepositoryPort {
    boolean existByDate(Date date);
    ReporteMenu findByDate(Date date);
    ReporteMenu create(ReporteMenu reporteMenu);
    ReporteMenu update(ReporteMenu reporteMenu);
    List<ReporteMenu> findByTimePeriod(Date start, Date end);
}

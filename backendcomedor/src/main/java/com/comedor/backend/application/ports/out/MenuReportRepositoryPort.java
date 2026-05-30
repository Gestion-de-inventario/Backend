package com.comedor.backend.application.ports.out;

import com.comedor.backend.domain.model.MenuReport;

import java.time.LocalDate;
import java.util.List;

public interface MenuReportRepositoryPort {
    boolean existByDate(LocalDate date);
    MenuReport findByDate(LocalDate date);
    MenuReport create(MenuReport menuReport);
    MenuReport update(MenuReport menuReport);
    List<MenuReport> findByTimePeriod(LocalDate start, LocalDate end);
    MenuReport findById(int id);
}

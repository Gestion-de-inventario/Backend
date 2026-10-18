package com.comedor.backend.application.ports.in;

import com.comedor.backend.domain.model.records.DailySoldMenusView;
import java.time.LocalDate;
import java.util.List;

public interface GetWeeklySoldMenusUseCase {
    List<DailySoldMenusView> getWeeklySoldMenus(LocalDate referenceDate);
}
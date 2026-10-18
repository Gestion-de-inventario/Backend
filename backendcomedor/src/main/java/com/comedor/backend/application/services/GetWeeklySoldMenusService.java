package com.comedor.backend.application.services;

import com.comedor.backend.application.ports.in.GetWeeklySoldMenusUseCase;
import com.comedor.backend.application.ports.out.MenuReportRepositoryPort;
import com.comedor.backend.domain.model.records.DailySoldMenusSummary;
import com.comedor.backend.domain.model.records.DailySoldMenusView;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class GetWeeklySoldMenusService implements GetWeeklySoldMenusUseCase {

    private final MenuReportRepositoryPort menuReportRepositoryPort;

    public GetWeeklySoldMenusService(MenuReportRepositoryPort menuReportRepositoryPort) {
        this.menuReportRepositoryPort = menuReportRepositoryPort;
    }

    @Override
    public List<DailySoldMenusView> getWeeklySoldMenus(LocalDate referenceDate) {
        LocalDate date = (referenceDate != null) ? referenceDate : LocalDate.now();

        LocalDate monday = date.with(DayOfWeek.MONDAY);
        LocalDate friday = date.with(DayOfWeek.FRIDAY);

        List<DailySoldMenusSummary> summaries = menuReportRepositoryPort.getDailySoldMenusInRange(monday, friday);

        Map<LocalDate, Integer> map = summaries.stream()
                .collect(Collectors.toMap(DailySoldMenusSummary::date, DailySoldMenusSummary::menusSold));

        String[] days = {"Lunes", "Martes", "Miércoles", "Jueves", "Viernes"};
        List<DailySoldMenusView> result = new ArrayList<>(5);

        for (int i = 0; i < 5; i++) {
            LocalDate currentDay = monday.plusDays(i);
            int sold = map.getOrDefault(currentDay, 0);
            result.add(new DailySoldMenusView(days[i], currentDay, sold));
        }

        return result;
    }
}
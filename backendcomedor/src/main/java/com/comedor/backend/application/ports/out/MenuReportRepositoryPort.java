package com.comedor.backend.application.ports.out;

import com.comedor.backend.domain.model.MenuReport;
import com.comedor.backend.domain.model.records.DailySoldMenusSummary;
import com.comedor.backend.domain.model.records.MonthlyFinancialSummary;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;

public interface MenuReportRepositoryPort {
    boolean existByDate(LocalDate date);
    MenuReport findByDate(LocalDate date);
    MenuReport create(MenuReport menuReport);
    MenuReport save(MenuReport menuReport);
    MenuReport update(MenuReport menuReport);
    Page<MenuReport> showMenuReport(
            LocalDate startDate,
            LocalDate endDate,
            Pageable pageable
    );

    List<MenuReport> showMenuReport(
            LocalDate startDate,
            LocalDate endDate
    );
    MenuReport findById(int id);

    List<MonthlyFinancialSummary> getMonthlyFinancialSummaryByYear(int year);
    List<DailySoldMenusSummary> getDailySoldMenusInRange(LocalDate startDate, LocalDate endDate);
}

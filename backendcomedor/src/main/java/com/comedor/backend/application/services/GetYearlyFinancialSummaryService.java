package com.comedor.backend.application.services;

import com.comedor.backend.application.ports.in.GetYearlyFinancialSummaryUseCase;
import com.comedor.backend.application.ports.out.MenuReportRepositoryPort;
import com.comedor.backend.domain.model.records.MonthlyFinancialSummary;
import com.comedor.backend.domain.model.records.MonthlyFinancialView;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class GetYearlyFinancialSummaryService implements GetYearlyFinancialSummaryUseCase {

    private final MenuReportRepositoryPort menuReportRepositoryPort;

    public GetYearlyFinancialSummaryService(MenuReportRepositoryPort menuReportRepositoryPort) {
        this.menuReportRepositoryPort = menuReportRepositoryPort;
    }

    @Override
    public List<MonthlyFinancialView> getSummaryByYear(int year) {
        List<MonthlyFinancialSummary> data = menuReportRepositoryPort.getMonthlyFinancialSummaryByYear(year);

        Map<Integer, MonthlyFinancialSummary> map = data.stream()
                .collect(Collectors.toMap(MonthlyFinancialSummary::month, m -> m));

        String[] months = {"Ene", "Feb", "Mar", "Abr", "May", "Jun", "Jul", "Ago", "Sep", "Oct", "Nov", "Dic"};
        List<MonthlyFinancialView> result = new ArrayList<>(12);

        for (int m = 1; m <= 12; m++) {
            MonthlyFinancialSummary item = map.get(m);
            BigDecimal income = item != null && item.totalIncome() != null ? item.totalIncome() : BigDecimal.ZERO;
            BigDecimal expense = item != null && item.totalExpense() != null ? item.totalExpense() : BigDecimal.ZERO;
            BigDecimal balance = income.subtract(expense);

            result.add(new MonthlyFinancialView(m, months[m - 1], income, expense, balance));
        }

        return result;
    }
}
package com.comedor.backend.application.ports.in;

import com.comedor.backend.domain.model.records.MonthlyFinancialView;
import java.util.List;

public interface GetYearlyFinancialSummaryUseCase {
    List<MonthlyFinancialView> getSummaryByYear(int year);
}
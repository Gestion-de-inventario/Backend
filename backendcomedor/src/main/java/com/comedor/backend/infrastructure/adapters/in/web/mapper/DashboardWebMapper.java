package com.comedor.backend.infrastructure.adapters.in.web.mapper;

import com.comedor.backend.domain.model.records.DailySoldMenusView;
import com.comedor.backend.domain.model.records.MonthlyFinancialView;
import com.comedor.backend.domain.model.records.TopBeneficiarySummary;
import com.comedor.backend.infrastructure.adapters.in.web.dto.response.TopBeneficiaryResponseDTO;
import com.comedor.backend.infrastructure.adapters.in.web.dto.response.WeeklySoldMenusResponseDTO;
import com.comedor.backend.infrastructure.adapters.in.web.dto.response.YearlyFinancialResponseDTO;
import org.springframework.stereotype.Component;

import java.util.List;
@Component
public class DashboardWebMapper {

    public TopBeneficiaryResponseDTO toTopBeneficiaryResponseDTO(TopBeneficiarySummary summary) {
        if (summary == null) return null;
        return new TopBeneficiaryResponseDTO(
                summary.beneficiaryId(),
                summary.dni(),
                summary.name(),
                summary.lastname(),
                summary.totalMenus()
        );
    }

    public List<TopBeneficiaryResponseDTO> toTopBeneficiaryResponseDTOList(List<TopBeneficiarySummary> summaries) {
        if (summaries == null) return List.of();
        return summaries.stream()
                .map(this::toTopBeneficiaryResponseDTO)
                .toList();
    }

    public YearlyFinancialResponseDTO toYearlyFinancialResponseDTO(MonthlyFinancialView view) {
        if (view == null) return null;
        return new YearlyFinancialResponseDTO(
                view.month(),
                view.monthName(),
                view.income(),
                view.expense(),
                view.balance()
        );
    }

    public List<YearlyFinancialResponseDTO> toYearlyFinancialResponseDTOList(List<MonthlyFinancialView> views) {
        if (views == null) return List.of();
        return views.stream()
                .map(this::toYearlyFinancialResponseDTO)
                .toList();
    }

    public WeeklySoldMenusResponseDTO toWeeklySoldMenusResponseDTO(DailySoldMenusView view) {
        if (view == null) return null;
        return new WeeklySoldMenusResponseDTO(
                view.dayName(),
                view.date(),
                view.menusSold()
        );
    }

    public List<WeeklySoldMenusResponseDTO> toWeeklySoldMenusResponseDTOList(List<DailySoldMenusView> views) {
        if (views == null) return List.of();
        return views.stream()
                .map(this::toWeeklySoldMenusResponseDTO)
                .toList();
    }
}
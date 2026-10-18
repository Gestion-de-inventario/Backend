package com.comedor.backend.application.ports.in;

import com.comedor.backend.domain.model.records.TopBeneficiarySummary;
import java.time.LocalDate;
import java.util.List;

public interface GetTopConsumersUseCase {
    List<TopBeneficiarySummary> getTopConsumers(LocalDate startDate, LocalDate endDate);
}
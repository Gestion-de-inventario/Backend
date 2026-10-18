package com.comedor.backend.application.services;

import com.comedor.backend.application.ports.in.GetTopConsumersUseCase;
import com.comedor.backend.application.ports.out.BeneficiaryControlRepositoryPort;
import com.comedor.backend.domain.model.records.TopBeneficiarySummary;
import java.time.LocalDate;
import java.util.List;

public class GetTopConsumersService implements GetTopConsumersUseCase {

    private final BeneficiaryControlRepositoryPort beneficiaryControlRepositoryPort;

    public GetTopConsumersService(BeneficiaryControlRepositoryPort beneficiaryControlRepositoryPort) {
        this.beneficiaryControlRepositoryPort = beneficiaryControlRepositoryPort;
    }

    @Override
    public List<TopBeneficiarySummary> getTopConsumers(LocalDate startDate, LocalDate endDate) {
        if (startDate == null || endDate == null) {
            throw new IllegalArgumentException("El rango de fechas no puede ser nulo");
        }
        if (startDate.isAfter(endDate)) {
            throw new IllegalArgumentException("La fecha de inicio no puede ser posterior a la fecha de fin");
        }
        return beneficiaryControlRepositoryPort.findTop5ConsumersByDateRange(startDate, endDate);
    }
}
package com.comedor.backend.application.ports.out;

import com.comedor.backend.domain.model.BeneficiaryControl;
import com.comedor.backend.domain.model.records.TopBeneficiarySummary;

import java.time.LocalDate;
import java.util.List;

public interface BeneficiaryControlRepositoryPort {
    BeneficiaryControl agregarBeneficiario(int reporteId, BeneficiaryControl control);

    BeneficiaryControl actualizarBeneficiario(int reporteId, int controlId, BeneficiaryControl control);


    BeneficiaryControl findById(int controlId);

    List<BeneficiaryControl> findByReporteId(int reporteId);

    List<TopBeneficiarySummary> findTop5ConsumersByDateRange(LocalDate startDate, LocalDate endDate);

}

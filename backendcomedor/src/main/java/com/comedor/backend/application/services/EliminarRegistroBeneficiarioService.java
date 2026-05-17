package com.comedor.backend.application.services;

import com.comedor.backend.application.ports.in.EliminarRegistroBeneficiarioUseCase;
import com.comedor.backend.application.ports.in.RecalcularResumenReporteUseCase;
import com.comedor.backend.application.ports.out.BeneficiaryControlRepositoryPort;
import org.springframework.transaction.annotation.Transactional;

public class EliminarRegistroBeneficiarioService implements EliminarRegistroBeneficiarioUseCase {
    private final BeneficiaryControlRepositoryPort
            beneficiaryControlRepositoryPort;

    private final RecalcularResumenReporteUseCase
            recalcularResumenReporteUseCase;

    public EliminarRegistroBeneficiarioService(
            BeneficiaryControlRepositoryPort beneficiaryControlRepositoryPort,
            RecalcularResumenReporteUseCase recalcularResumenReporteUseCase
    ) {
        this.beneficiaryControlRepositoryPort =
                beneficiaryControlRepositoryPort;

        this.recalcularResumenReporteUseCase =
                recalcularResumenReporteUseCase;
    }
    @Transactional
    @Override
    public void eliminarRegistroBeneficiario(
            int reporteId,
            int controlId
    ) {

        beneficiaryControlRepositoryPort
                .eliminarBeneficiario(reporteId,controlId);

        recalcularResumenReporteUseCase
                .recalcular(reporteId);
    }
}

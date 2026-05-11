package com.comedor.backend.application.services;

import com.comedor.backend.application.ports.in.EliminarRegistroBeneficiarioUseCase;
import com.comedor.backend.application.ports.in.RecalcularResumenReporteUseCase;
import com.comedor.backend.application.ports.out.ControlBeneficiarioRepositoryPort;
import org.springframework.transaction.annotation.Transactional;

public class EliminarRegistroBeneficiarioService implements EliminarRegistroBeneficiarioUseCase {
    private final ControlBeneficiarioRepositoryPort
            controlBeneficiarioRepositoryPort;

    private final RecalcularResumenReporteUseCase
            recalcularResumenReporteUseCase;

    public EliminarRegistroBeneficiarioService(
            ControlBeneficiarioRepositoryPort controlBeneficiarioRepositoryPort,
            RecalcularResumenReporteUseCase recalcularResumenReporteUseCase
    ) {
        this.controlBeneficiarioRepositoryPort =
                controlBeneficiarioRepositoryPort;

        this.recalcularResumenReporteUseCase =
                recalcularResumenReporteUseCase;
    }
    @Transactional
    @Override
    public void eliminarRegistroBeneficiario(
            int reporteId,
            int controlId
    ) {

        controlBeneficiarioRepositoryPort
                .eliminarBeneficiario(reporteId,controlId);

        recalcularResumenReporteUseCase
                .recalcular(reporteId);
    }
}

package com.comedor.backend.application.services;

import com.comedor.backend.application.ports.in.EliminarRegistroBeneficiarioUseCase;
import com.comedor.backend.application.ports.in.RecalcularResumenReporteUseCase;
import com.comedor.backend.application.ports.out.BeneficiaryControlRepositoryPort;
import com.comedor.backend.application.ports.out.MenuReportRepositoryPort;
import com.comedor.backend.domain.model.BeneficiaryControl;
import com.comedor.backend.domain.model.MenuReport;
import org.springframework.transaction.annotation.Transactional;

public class EliminarRegistroBeneficiarioService implements EliminarRegistroBeneficiarioUseCase {
    private final BeneficiaryControlRepositoryPort
            beneficiaryControlRepositoryPort;

    private final RecalcularResumenReporteUseCase
            recalcularResumenReporteUseCase;
    private final MenuReportRepositoryPort menuReportRepositoryPort;

    public EliminarRegistroBeneficiarioService(
            BeneficiaryControlRepositoryPort beneficiaryControlRepositoryPort,
            RecalcularResumenReporteUseCase recalcularResumenReporteUseCase, MenuReportRepositoryPort menuReportRepositoryPort
    ) {
        this.beneficiaryControlRepositoryPort =
                beneficiaryControlRepositoryPort;

        this.recalcularResumenReporteUseCase =
                recalcularResumenReporteUseCase;
        this.menuReportRepositoryPort = menuReportRepositoryPort;
    }

    @Transactional
    @Override
    public void eliminarRegistroBeneficiario(
            int reporteId,
            int controlId
    ) {
        BeneficiaryControl actual =
                beneficiaryControlRepositoryPort.findById(controlId);

        MenuReport report =
                menuReportRepositoryPort.findById(reporteId);

        report.setQuantityRemaining(
                report.getQuantityRemaining()
                        + actual.getMenusAmount()
        );
        report.removeBeneficiaryControl(controlId);

        menuReportRepositoryPort.update(report);

        recalcularResumenReporteUseCase
                .recalcular(reporteId);
    }
}

package com.comedor.backend.application.services;

import com.comedor.backend.application.ports.in.RecalcularResumenReporteUseCase;
import com.comedor.backend.application.ports.out.ControlBeneficiarioRepositoryPort;
import com.comedor.backend.application.ports.out.RegistroProductoRepositoryPort;
import com.comedor.backend.application.ports.out.ReporteMenuRepositoryPort;
import com.comedor.backend.domain.model.ControlBeneficiario;
import com.comedor.backend.domain.model.Registro;
import com.comedor.backend.domain.model.ReporteMenu;

import java.math.BigDecimal;
import java.util.List;

public class RecalcularResumenReporteService
        implements RecalcularResumenReporteUseCase {

    private final ReporteMenuRepositoryPort
            reporteMenuRepositoryPort;

    private final ControlBeneficiarioRepositoryPort
            controlBeneficiarioRepositoryPort;

    private final RegistroProductoRepositoryPort
            registroProductoRepositoryPort;

    public RecalcularResumenReporteService(ReporteMenuRepositoryPort reporteMenuRepositoryPort, ControlBeneficiarioRepositoryPort controlBeneficiarioRepositoryPort, RegistroProductoRepositoryPort registroProductoRepositoryPort) {
        this.reporteMenuRepositoryPort = reporteMenuRepositoryPort;
        this.controlBeneficiarioRepositoryPort = controlBeneficiarioRepositoryPort;
        this.registroProductoRepositoryPort = registroProductoRepositoryPort;
    }


    @Override
    public void recalcular(int reporteId) {
        ReporteMenu reporte =
                reporteMenuRepositoryPort
                        .findById(reporteId);

        List<Registro> registros =
                registroProductoRepositoryPort
                        .findByReporteId(reporteId);

        List<ControlBeneficiario> beneficiarios =
                controlBeneficiarioRepositoryPort
                        .findByReporteId(reporteId);

        BigDecimal totalSpent =
                registros.stream()
                        .map(r ->
                                r.getUnitPrice()
                                        .multiply(r.getAmount())
                        )
                        .reduce(
                                BigDecimal.ZERO,
                                BigDecimal::add
                        );

        BigDecimal totalEarned =
                beneficiarios.stream()
                        .filter(ControlBeneficiario::getPaid)
                        .map(c ->
                                c.getMenuPrice().multiply(
                                        BigDecimal.valueOf(
                                                c.getMenusAmount()
                                        )
                                )
                        )
                        .reduce(
                                BigDecimal.ZERO,
                                BigDecimal::add
                        );

        reporte.setTotalSpent(totalSpent);

        reporte.setTotalEarned(totalEarned);

        reporteMenuRepositoryPort.update(reporte);
    }
}
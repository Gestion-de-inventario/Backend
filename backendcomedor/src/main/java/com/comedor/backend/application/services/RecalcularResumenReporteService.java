package com.comedor.backend.application.services;

import com.comedor.backend.application.ports.in.RecalcularResumenReporteUseCase;
import com.comedor.backend.application.ports.out.BeneficiaryControlRepositoryPort;
import com.comedor.backend.application.ports.out.ProductRecordRepositoryPort;
import com.comedor.backend.application.ports.out.MenuReportRepositoryPort;
import com.comedor.backend.domain.model.BeneficiaryControl;
import com.comedor.backend.domain.model.Record;
import com.comedor.backend.domain.model.MenuReport;

import java.math.BigDecimal;
import java.util.List;

public class RecalcularResumenReporteService
        implements RecalcularResumenReporteUseCase {

    private final MenuReportRepositoryPort
            menuReportRepositoryPort;

    private final BeneficiaryControlRepositoryPort
            beneficiaryControlRepositoryPort;

    private final ProductRecordRepositoryPort
            productRecordRepositoryPort;

    public RecalcularResumenReporteService(MenuReportRepositoryPort menuReportRepositoryPort, BeneficiaryControlRepositoryPort beneficiaryControlRepositoryPort, ProductRecordRepositoryPort productRecordRepositoryPort) {
        this.menuReportRepositoryPort = menuReportRepositoryPort;
        this.beneficiaryControlRepositoryPort = beneficiaryControlRepositoryPort;
        this.productRecordRepositoryPort = productRecordRepositoryPort;
    }


    @Override
    public void recalcular(int reporteId) {
        MenuReport reporte =
                menuReportRepositoryPort
                        .findById(reporteId);

        List<Record> records =
                productRecordRepositoryPort
                        .findByReporteId(reporteId);

        List<BeneficiaryControl> beneficiarios =
                beneficiaryControlRepositoryPort
                        .findByReporteId(reporteId);

        BigDecimal totalSpent =
                records.stream()
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
                        .filter(BeneficiaryControl::getPaid)
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

        menuReportRepositoryPort.update(reporte);
    }
}
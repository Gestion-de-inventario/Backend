package com.comedor.backend.application.services;

import com.comedor.backend.application.ports.in.DeleteBeneficiaryRecordUseCase;
import com.comedor.backend.application.ports.in.RecalculateSummaryReportUseCase;
import com.comedor.backend.application.ports.in.RegisterTransactionUseCase;
import com.comedor.backend.application.ports.out.BeneficiaryControlRepositoryPort;
import com.comedor.backend.application.ports.out.MenuReportRepositoryPort;
import com.comedor.backend.domain.model.BeneficiaryControl;
import com.comedor.backend.domain.model.MenuReport;
import com.comedor.backend.domain.model.enums.MovementType;
import com.comedor.backend.domain.model.enums.TransactionReferenceType;
import com.comedor.backend.domain.model.enums.TransactionSource;
import com.comedor.backend.infrastructure.adapters.in.web.dto.request.TransactionRequestDTO;
import com.comedor.backend.infrastructure.config.PeruTime;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class DeleteBeneficiaryRecordService implements DeleteBeneficiaryRecordUseCase {
    private final BeneficiaryControlRepositoryPort
            beneficiaryControlRepositoryPort;

    private final RecalculateSummaryReportUseCase
            recalcularResumenReporteUseCase;
    private final MenuReportRepositoryPort menuReportRepositoryPort;

    private final RegisterTransactionUseCase registerTransactionUseCase;

    private final CurrentUserService currentUserService;

    public DeleteBeneficiaryRecordService(
            BeneficiaryControlRepositoryPort beneficiaryControlRepositoryPort,
            RecalculateSummaryReportUseCase recalcularResumenReporteUseCase, MenuReportRepositoryPort menuReportRepositoryPort, RegisterTransactionUseCase registerTransactionUseCase, CurrentUserService currentUserService
    ) {
        this.beneficiaryControlRepositoryPort =
                beneficiaryControlRepositoryPort;

        this.recalcularResumenReporteUseCase =
                recalcularResumenReporteUseCase;
        this.menuReportRepositoryPort = menuReportRepositoryPort;
        this.registerTransactionUseCase = registerTransactionUseCase;
        this.currentUserService = currentUserService;
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

        Integer userId =
                currentUserService.getCurrentUser().getId();

        registrarMovimiento(
                userId,
                //Ojito con el refactor dinamico ,
                // aqui seria consultar el name directamente
                report.getDishMenu().getName(),
                BigDecimal.valueOf(actual.getMenusAmount()),
                BigDecimal.valueOf(report.getQuantityRemaining()),
                PeruTime.now()
        );


        report.removeBeneficiaryControl(controlId);

        menuReportRepositoryPort.update(report);

        recalcularResumenReporteUseCase
                .recalcular(reporteId);
    }

    private void registrarMovimiento(
            Integer usuarioId,
            String itemName,
            BigDecimal amount,
            BigDecimal currentStock,
            LocalDateTime localDateTime
    ) {
        TransactionRequestDTO dto = new TransactionRequestDTO();

        dto.setReferenceType(TransactionReferenceType.MENU);
        dto.setReferenceId(null);
        dto.setItemName(itemName);
        dto.setType(MovementType.ENTRADA);
        dto.setAmount(amount);
        dto.setCurrentStock(currentStock);
        dto.setSource(TransactionSource.INVENTARIO);
        dto.setUserId(usuarioId);
        dto.setDateTime(localDateTime);

        registerTransactionUseCase.registrarTransaccion(dto);
    }
}

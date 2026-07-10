package com.comedor.backend.application.services;

import com.comedor.backend.application.common.mapper.BeneficiaryControlMapper;
import com.comedor.backend.application.ports.in.EditBeneficiaryRecordUseCase;
import com.comedor.backend.application.ports.in.RecalculateSummaryReportUseCase;
import com.comedor.backend.application.ports.in.RegisterTransactionUseCase;
import com.comedor.backend.application.ports.out.BeneficiaryControlRepositoryPort;
import com.comedor.backend.application.ports.out.MenuReportRepositoryPort;
import com.comedor.backend.domain.model.BeneficiaryControl;
import com.comedor.backend.domain.model.MenuReport;
import com.comedor.backend.domain.model.enums.MovementType;
import com.comedor.backend.domain.model.enums.TransactionReferenceType;
import com.comedor.backend.domain.model.enums.TransactionSource;
import com.comedor.backend.infrastructure.adapters.in.web.dto.request.ControlBeneficiarioRequestDTO;
import com.comedor.backend.infrastructure.adapters.in.web.dto.request.TransactionRequestDTO;
import com.comedor.backend.infrastructure.adapters.in.web.dto.response.BeneficiaryRecordResponseDTO;
import com.comedor.backend.infrastructure.config.PeruTime;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Transactional
public class EditBeneficiaryRecordService implements EditBeneficiaryRecordUseCase {
    private final BeneficiaryControlRepositoryPort beneficiaryControlRepositoryPort;
    private final BeneficiaryControlMapper beneficiaryControlMapper;
    private final RecalculateSummaryReportUseCase recalcularResumenReporteUseCase;
    private final MenuReportRepositoryPort menuReportRepositoryPort;
    private final RegisterTransactionUseCase registerTransactionUseCase;
    private final CurrentUserService currentUserService;
    public EditBeneficiaryRecordService(BeneficiaryControlRepositoryPort beneficiaryControlRepositoryPort, BeneficiaryControlMapper beneficiaryControlMapper, RecalculateSummaryReportUseCase recalcularResumenReporteUseCase, MenuReportRepositoryPort menuReportRepositoryPort, RegisterTransactionUseCase registerTransactionUseCase, CurrentUserService currentUserService) {
        this.beneficiaryControlRepositoryPort = beneficiaryControlRepositoryPort;
        this.beneficiaryControlMapper = beneficiaryControlMapper;
        this.recalcularResumenReporteUseCase = recalcularResumenReporteUseCase;
        this.menuReportRepositoryPort = menuReportRepositoryPort;
        this.registerTransactionUseCase = registerTransactionUseCase;
        this.currentUserService = currentUserService;
    }

    @Override
    public BeneficiaryRecordResponseDTO editarRegistroBeneficiario(int reporteId, int controlId, ControlBeneficiarioRequestDTO dto) {

        BeneficiaryControl actual =
                beneficiaryControlRepositoryPort
                        .findById(controlId);

        if(dto.getPago() != null)
        {
            actual.setPaid(dto.getPago());
        }

        if(dto.getEntregado() != null)
        {
            actual.setReceived(dto.getEntregado());
        }

        if(dto.getPayMethod() != null)
        {
            actual.setPayMethod(dto.getPayMethod());
        }

        if (dto.getMenusAmount() != null) {

            int oldAmount = actual.getMenusAmount();
            int newAmount = dto.getMenusAmount();

            int difference = newAmount - oldAmount;

            MenuReport report =
                    menuReportRepositoryPort.findById(reporteId);

            if (difference > 0) {

                if (report.getQuantityRemaining() < difference) {
                    throw new RuntimeException(
                            "Solo quedan " + report.getQuantityRemaining()
                                    + " menus disponibles"
                    );
                }

                report.setQuantityRemaining(
                        report.getQuantityRemaining() - difference
                );
            }

            if (difference < 0) {
                report.setQuantityRemaining(
                        report.getQuantityRemaining() + Math.abs(difference)
                );
            }

            actual.setMenusAmount(newAmount);

            Integer userId =
                    currentUserService.getCurrentUser().getId();
            registrarMovimiento(
                    userId,
                    //Ojito con el refactor dinamico ,
                    // aqui seria consultar el name directamente
                    report.getDishMenu().getName(),
                    BigDecimal.valueOf(newAmount),
                    BigDecimal.valueOf(oldAmount),
                    PeruTime.now()
            );

            menuReportRepositoryPort.update(report);
        }

        if(dto.getMenuPrice() != null)
        {
            actual.setMenuPrice(dto.getMenuPrice());
        }

        BeneficiaryControl actualizado =
                beneficiaryControlRepositoryPort
                        .actualizarBeneficiario(
                                reporteId,
                                controlId,
                                actual
                        );

        recalcularResumenReporteUseCase.recalcular(reporteId);

        return beneficiaryControlMapper
                .toDto(actualizado);
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
        dto.setType(MovementType.MODIFICACION);
        dto.setAmount(amount);
        dto.setCurrentStock(currentStock);
        dto.setSource(TransactionSource.INVENTARIO);
        dto.setUserId(usuarioId);
        dto.setDateTime(localDateTime);

        registerTransactionUseCase.registrarTransaccion(dto);
    }

}

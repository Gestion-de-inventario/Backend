package com.comedor.backend.application.services;


import com.comedor.backend.application.common.mapper.BeneficiaryControlMapper;
import com.comedor.backend.application.ports.in.AddRecordBeneficiaryUseCase;
import com.comedor.backend.application.ports.in.RecalculateSummaryReportUseCase;
import com.comedor.backend.application.ports.in.RegisterTransactionUseCase;
import com.comedor.backend.application.ports.out.BeneficiaryControlRepositoryPort;
import com.comedor.backend.application.ports.out.BeneficiaryRepositoryPort;
import com.comedor.backend.application.ports.out.MenuReportRepositoryPort;
import com.comedor.backend.domain.exceptions.BeneficiaryNotFoundException;
import com.comedor.backend.domain.exceptions.QuantityMenuInvalidException;
import com.comedor.backend.domain.exceptions.MenuPriceInvalidException;
import com.comedor.backend.domain.model.Beneficiary;
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


public class AddRecordBeneficiaryService implements AddRecordBeneficiaryUseCase {

    private final BeneficiaryControlRepositoryPort beneficiaryControlRepositoryPort;

    private final BeneficiaryControlMapper beneficiaryControlMapper;

    private final BeneficiaryRepositoryPort beneficiaryRepositoryPort;

    private final RecalculateSummaryReportUseCase recalcularResumenReporteUseCase;

    private final MenuReportRepositoryPort menuReportRepositoryPort;

    private final RegisterTransactionUseCase registerTransactionUseCase;

    private final CurrentUserService currentUserService;

    public AddRecordBeneficiaryService(
            BeneficiaryControlRepositoryPort beneficiaryControlRepositoryPort,
            BeneficiaryControlMapper beneficiaryControlMapper, BeneficiaryRepositoryPort beneficiaryRepositoryPort,
            RecalculateSummaryReportUseCase recalcularResumenReporteUseCase, MenuReportRepositoryPort menuReportRepositoryPort, RegisterTransactionUseCase registerTransactionUseCase, CurrentUserService currentUserService
    ) {
        this.beneficiaryControlRepositoryPort =
                beneficiaryControlRepositoryPort;

        this.beneficiaryControlMapper =
                beneficiaryControlMapper;
        this.beneficiaryRepositoryPort = beneficiaryRepositoryPort;

        this.recalcularResumenReporteUseCase =
                recalcularResumenReporteUseCase;
        this.menuReportRepositoryPort = menuReportRepositoryPort;
        this.registerTransactionUseCase = registerTransactionUseCase;
        this.currentUserService = currentUserService;
    }

    @Override
    @Transactional
    public BeneficiaryRecordResponseDTO
    agregarRegistroBeneficiario(
            int reporteId,
            ControlBeneficiarioRequestDTO dto
    ) {

        validar(dto);

        MenuReport report =
                menuReportRepositoryPort.findById(reporteId);

        Integer actualQuantity=report.getQuantityRemaining();

        if (report.getQuantityRemaining() < dto.getMenusAmount()) {
            throw new RuntimeException(
                    "No hay suficientes menus disponibles. Quedan: "
                            + report.getQuantityRemaining()
            );
        }

        BeneficiaryControl control =
                beneficiaryControlMapper
                        .toDomain(dto);
        Beneficiary beneficiary = beneficiaryRepositoryPort.findById(dto.getBeneficiarioId()).orElseThrow(()-> new BeneficiaryNotFoundException("Beneficiario no encontrado"));

        control.setBeneficiario(beneficiary);

        BeneficiaryControl creado =
                beneficiaryControlRepositoryPort
                        .agregarBeneficiario(
                                reporteId,
                                control
                        );

        report.setQuantityRemaining(
                report.getQuantityRemaining() - dto.getMenusAmount()
        );

        Integer usuarioId =
                currentUserService.getCurrentUser().getId();

        registrarMovimiento(
                usuarioId,
                //Ojito con el refactor dinamico ,
                // aqui seria consultar el name directamente
                report.getDishMenu().getName(),
                BigDecimal.valueOf(dto.getMenusAmount()),
                BigDecimal.valueOf(actualQuantity),
                PeruTime.now()
        );

        menuReportRepositoryPort.update(report);

        recalcularResumenReporteUseCase
                .recalcular(reporteId);

        return beneficiaryControlMapper
                .toDto(creado);
    }

    private void validar(
            ControlBeneficiarioRequestDTO dto
    ) {

        if(dto.getMenusAmount() == null){
            throw new QuantityMenuInvalidException(
                    "La cantidad de menus es obligatoria"
            );
        }

        if(dto.getMenusAmount() <= 0){
            throw new QuantityMenuInvalidException(
                    "La cantidad de menus debe ser mayor a 0"
            );
        }

        if(dto.getMenuPrice() == null){
            throw new MenuPriceInvalidException(
                    "El precio del menu es obligatorio"
            );
        }

        if(dto.getMenuPrice()
                .compareTo(BigDecimal.ZERO) < 0){

            throw new MenuPriceInvalidException(
                    "El precio no puede ser negativo"
            );
        }
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
        dto.setType(MovementType.SALIDA);
        dto.setAmount(amount);
        dto.setCurrentStock(currentStock);
        dto.setSource(TransactionSource.INVENTARIO);
        dto.setUserId(usuarioId);
        dto.setDateTime(localDateTime);

        registerTransactionUseCase.registrarTransaccion(dto);
    }
}

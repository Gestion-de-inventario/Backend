package com.comedor.backend.application.services;

import com.comedor.backend.application.common.mapper.ProductRecordMapper;
import com.comedor.backend.application.ports.in.UpdateStockUseCase;
import com.comedor.backend.application.ports.in.EditProductRegistryUseCase;
import com.comedor.backend.application.ports.in.RecalculateSummaryReportUseCase;
import com.comedor.backend.application.ports.in.RegisterTransactionUseCase;
import com.comedor.backend.application.ports.out.ProductRecordRepositoryPort;
import com.comedor.backend.domain.model.Record;
import com.comedor.backend.domain.model.enums.ProductSource;
import com.comedor.backend.domain.model.enums.MovementType;
import com.comedor.backend.infrastructure.adapters.in.web.dto.request.ProductRecordRequestDTO;
import com.comedor.backend.infrastructure.adapters.in.web.dto.request.TransactionRequestDTO;
import com.comedor.backend.infrastructure.adapters.in.web.dto.response.ProductRecordResponseDTO;
import com.comedor.backend.infrastructure.config.PeruTime;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class EditProductRegistryService implements EditProductRegistryUseCase {
    private final ProductRecordRepositoryPort
            productRecordRepositoryPort;

    private final ProductRecordMapper
            productRecordMapper;

    private final UpdateStockUseCase
            updateStockUseCase;

    private final RegisterTransactionUseCase
            registerTransactionUseCase;

    private final RecalculateSummaryReportUseCase
            recalcularResumenReporteUseCase;

    private final CurrentUserService
            currentUserService;

    public EditProductRegistryService(
            ProductRecordRepositoryPort productRecordRepositoryPort,
            ProductRecordMapper productRecordMapper,
            UpdateStockUseCase updateStockUseCase,
            RegisterTransactionUseCase registerTransactionUseCase,
            RecalculateSummaryReportUseCase recalcularResumenReporteUseCase,
            CurrentUserService currentUserService
    ) {
        this.productRecordRepositoryPort =
                productRecordRepositoryPort;

        this.productRecordMapper =
                productRecordMapper;

        this.updateStockUseCase =
                updateStockUseCase;

        this.registerTransactionUseCase =
                registerTransactionUseCase;

        this.recalcularResumenReporteUseCase =
                recalcularResumenReporteUseCase;

        this.currentUserService =
                currentUserService;
    }
    @Transactional
    @Override
    public ProductRecordResponseDTO editarRegistroProducto(
            int reporteId,
            int registroId,
            ProductRecordRequestDTO dto
    ) {

        // OBTENER REGISTRO ACTUAL
        Record actual =
                productRecordRepositoryPort
                        .findById(registroId);

        Integer usuarioId =
                currentUserService
                        .getCurrentUser()
                        .getId();

        revertirMovimientoAnterior(
                actual,
                usuarioId
        );

        Record nuevo =
                productRecordMapper
                        .toDomain(dto);

        Record actualizado =
                productRecordRepositoryPort
                        .actualizarRegistroProducto(
                                reporteId,
                                registroId,
                                nuevo
                        );

        aplicarNuevoMovimiento(
                actualizado,
                usuarioId
        );


        recalcularResumenReporteUseCase
                .recalcular(reporteId);

        return productRecordMapper
                .toDto(actualizado);
    }


    private void revertirMovimientoAnterior(
            Record actual,
            Integer usuarioId
    ) {

        updateStockUseCase.actualizarStock(
                actual.getProduct().getId(),
                actual.getAmount(),
                MovementType.ENTRADA
        );

        registrarMovimiento(
                usuarioId,
                actual.getProduct().getId(),
                actual.getAmount(),
                MovementType.ENTRADA,
                PeruTime.now()
        );
    }



    private void aplicarNuevoMovimiento(
            Record record,
            Integer usuarioId
    ) {

        if(record.getProductSource()
                == ProductSource.COMPRA){

            updateStockUseCase.actualizarStock(
                    record.getProduct().getId(),
                    record.getAmount(),
                    MovementType.ENTRADA
            );

            registrarMovimiento(
                    usuarioId,
                    record.getProduct().getId(),
                    record.getAmount(),
                    MovementType.ENTRADA,
                    PeruTime.now()
            );
        }

        updateStockUseCase.actualizarStock(
                record.getProduct().getId(),
                record.getAmount(),
                MovementType.SALIDA
        );

        registrarMovimiento(
                usuarioId,
                record.getProduct().getId(),
                record.getAmount(),
                MovementType.SALIDA,
                PeruTime.now()
        );
    }


    private void registrarMovimiento(
            Integer usuarioId,
            Integer productoId,
            BigDecimal amount,
            MovementType tipo,
            LocalDateTime dateTime
    )
    {

        TransactionRequestDTO dto =new TransactionRequestDTO();

        dto.setUserId(usuarioId);
        dto.setProductId(productoId);
        dto.setAmount(amount);
        dto.setType(tipo);
        dto.setDateTime(dateTime);
        registerTransactionUseCase.registrarTransaccion(dto);
    }
}

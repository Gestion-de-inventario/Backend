package com.comedor.backend.application.services;

import com.comedor.backend.application.ports.in.DeleteProductRecordUseCase;
import com.comedor.backend.application.ports.in.RecalculateSummaryReportUseCase;
import com.comedor.backend.application.ports.in.RegisterTransactionUseCase;
import com.comedor.backend.application.ports.out.ProductRecordRepositoryPort;
import com.comedor.backend.domain.model.Record;
import com.comedor.backend.domain.model.enums.ProductSource;
import com.comedor.backend.domain.model.enums.MovementType;
import com.comedor.backend.infrastructure.adapters.in.web.dto.request.TransactionRequestDTO;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

public class DeleteProductRecordService implements DeleteProductRecordUseCase {
    private final ProductRecordRepositoryPort
            productRecordRepositoryPort;

    private final RegisterTransactionUseCase
            registerTransactionUseCase;

    private final CurrentUserService
            currentUserService;

    private final RecalculateSummaryReportUseCase
            recalcularResumenReporteUseCase;

    public DeleteProductRecordService(
            ProductRecordRepositoryPort productRecordRepositoryPort,
            RegisterTransactionUseCase registerTransactionUseCase,
            CurrentUserService currentUserService,
            RecalculateSummaryReportUseCase recalcularResumenReporteUseCase
    ) {
        this.productRecordRepositoryPort =
                productRecordRepositoryPort;

        this.registerTransactionUseCase =
                registerTransactionUseCase;

        this.currentUserService =
                currentUserService;

        this.recalcularResumenReporteUseCase =
                recalcularResumenReporteUseCase;
    }

    @Override
    @Transactional
    public void eliminarRegistroProducto(
            int reporteId,
            int registroId
    ) {

        // 1. OBTENER REGISTRO ACTUAL
        Record record =
                productRecordRepositoryPort
                        .findById(registroId);

        // VALIDAR QUE PERTENECE AL REPORTE

        Integer usuarioId =
                currentUserService
                        .getCurrentUser()
                        .getId();

        /*
         SI EL REGISTRO ERA DONACION/ALMACEN
         SE HABÍA HECHO UNA SALIDA.

         AL ELIMINAR:
         debemos DEVOLVER stock
         => ENTRADA
        */

        registrarMovimiento(
                usuarioId,
                record.getProduct().getId(),
                record.getAmount(),
                MovementType.ENTRADA
        );

        /*
         SI ERA COMPRA:
         al crear se hizo:
         ENTRADA + SALIDA

         neto = 0

         al eliminar:
         debemos revertir ambas:
         SALIDA + ENTRADA

         aunque realmente se anulan.
        */

        if (record.getProductSource()
                == ProductSource.COMPRA) {

            registrarMovimiento(
                    usuarioId,
                    record.getProduct().getId(),
                    record.getAmount(),
                    MovementType.SALIDA
            );
        }

        // ELIMINAR REGISTRO
        productRecordRepositoryPort
                .eliminarRegistroProducto(
                        reporteId,
                        registroId
                );

        // RECALCULAR TOTALES
        recalcularResumenReporteUseCase
                .recalcular(reporteId);
    }

    private void registrarMovimiento(
            Integer usuarioId,
            Integer productoId,
            BigDecimal amount,
            MovementType tipo
    ) {

        TransactionRequestDTO dto =
                new TransactionRequestDTO();

        dto.setUserId(usuarioId);
        dto.setProductId(productoId);
        dto.setAmount(amount);
        dto.setType(tipo);

        registerTransactionUseCase
                .registrarTransaccion(dto);
    }
}

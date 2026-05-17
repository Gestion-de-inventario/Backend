package com.comedor.backend.application.services;

import com.comedor.backend.application.ports.in.EliminarRegistroProductoUseCase;
import com.comedor.backend.application.ports.in.RecalcularResumenReporteUseCase;
import com.comedor.backend.application.ports.in.RegistrarTransaccionUseCase;
import com.comedor.backend.application.ports.out.ProductRecordRepositoryPort;
import com.comedor.backend.domain.model.Record;
import com.comedor.backend.domain.model.enums.FuenteProducto;
import com.comedor.backend.domain.model.enums.TipoMovimiento;
import com.comedor.backend.infrastructure.adapters.in.web.dto.request.TransaccionRequestDTO;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

public class EliminarRegistroProductoService implements EliminarRegistroProductoUseCase {
    private final ProductRecordRepositoryPort
            productRecordRepositoryPort;

    private final RegistrarTransaccionUseCase
            registrarTransaccionUseCase;

    private final CurrentUserService
            currentUserService;

    private final RecalcularResumenReporteUseCase
            recalcularResumenReporteUseCase;

    public EliminarRegistroProductoService(
            ProductRecordRepositoryPort productRecordRepositoryPort,
            RegistrarTransaccionUseCase registrarTransaccionUseCase,
            CurrentUserService currentUserService,
            RecalcularResumenReporteUseCase recalcularResumenReporteUseCase
    ) {
        this.productRecordRepositoryPort =
                productRecordRepositoryPort;

        this.registrarTransaccionUseCase =
                registrarTransaccionUseCase;

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
                TipoMovimiento.ENTRADA
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
                == FuenteProducto.COMPRA) {

            registrarMovimiento(
                    usuarioId,
                    record.getProduct().getId(),
                    record.getAmount(),
                    TipoMovimiento.SALIDA
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
            TipoMovimiento tipo
    ) {

        TransaccionRequestDTO dto =
                new TransaccionRequestDTO();

        dto.setUserId(usuarioId);
        dto.setProductId(productoId);
        dto.setAmount(amount);
        dto.setType(tipo);

        registrarTransaccionUseCase
                .registrarTransaccion(dto);
    }
}

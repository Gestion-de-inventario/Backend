package com.comedor.backend.application.services;

import com.comedor.backend.application.ports.in.EliminarRegistroProductoUseCase;
import com.comedor.backend.application.ports.in.RecalcularResumenReporteUseCase;
import com.comedor.backend.application.ports.in.RegistrarTransaccionUseCase;
import com.comedor.backend.application.ports.out.RegistroProductoRepositoryPort;
import com.comedor.backend.domain.model.Registro;
import com.comedor.backend.domain.model.enums.FuenteProducto;
import com.comedor.backend.domain.model.enums.TipoMovimiento;
import com.comedor.backend.infrastructure.adapters.in.web.dto.request.TransaccionRequestDTO;
import jakarta.transaction.Transactional;

import java.math.BigDecimal;

public class EliminarRegistroProductoService implements EliminarRegistroProductoUseCase {
    private final RegistroProductoRepositoryPort
            registroProductoRepositoryPort;

    private final RegistrarTransaccionUseCase
            registrarTransaccionUseCase;

    private final CurrentUserService
            currentUserService;

    private final RecalcularResumenReporteUseCase
            recalcularResumenReporteUseCase;

    public EliminarRegistroProductoService(
            RegistroProductoRepositoryPort registroProductoRepositoryPort,
            RegistrarTransaccionUseCase registrarTransaccionUseCase,
            CurrentUserService currentUserService,
            RecalcularResumenReporteUseCase recalcularResumenReporteUseCase
    ) {
        this.registroProductoRepositoryPort =
                registroProductoRepositoryPort;

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
        Registro registro =
                registroProductoRepositoryPort
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
                registro.getProduct().getId(),
                registro.getAmount(),
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

        if (registro.getFuenteProducto()
                == FuenteProducto.COMPRA) {

            registrarMovimiento(
                    usuarioId,
                    registro.getProduct().getId(),
                    registro.getAmount(),
                    TipoMovimiento.SALIDA
            );
        }

        // ELIMINAR REGISTRO
        registroProductoRepositoryPort
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

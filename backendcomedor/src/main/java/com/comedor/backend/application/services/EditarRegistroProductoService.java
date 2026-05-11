package com.comedor.backend.application.services;

import com.comedor.backend.application.common.mapper.RegistroProductoMapper;
import com.comedor.backend.application.ports.in.ActualizarStockUseCase;
import com.comedor.backend.application.ports.in.EditarRegistroProductoUseCase;
import com.comedor.backend.application.ports.in.RecalcularResumenReporteUseCase;
import com.comedor.backend.application.ports.in.RegistrarTransaccionUseCase;
import com.comedor.backend.application.ports.out.RegistroProductoRepositoryPort;
import com.comedor.backend.domain.model.Registro;
import com.comedor.backend.domain.model.enums.FuenteProducto;
import com.comedor.backend.domain.model.enums.TipoMovimiento;
import com.comedor.backend.infrastructure.adapters.in.web.dto.request.RegistroProductoRequestDTO;
import com.comedor.backend.infrastructure.adapters.in.web.dto.request.TransaccionRequestDTO;
import com.comedor.backend.infrastructure.adapters.in.web.dto.response.RegistroProductoResponseDTO;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

public class EditarRegistroProductoService implements EditarRegistroProductoUseCase {
    private final RegistroProductoRepositoryPort
            registroProductoRepositoryPort;

    private final RegistroProductoMapper
            registroProductoMapper;

    private final ActualizarStockUseCase
            actualizarStockUseCase;

    private final RegistrarTransaccionUseCase
            registrarTransaccionUseCase;

    private final RecalcularResumenReporteUseCase
            recalcularResumenReporteUseCase;

    private final CurrentUserService
            currentUserService;

    public EditarRegistroProductoService(
            RegistroProductoRepositoryPort registroProductoRepositoryPort,
            RegistroProductoMapper registroProductoMapper,
            ActualizarStockUseCase actualizarStockUseCase,
            RegistrarTransaccionUseCase registrarTransaccionUseCase,
            RecalcularResumenReporteUseCase recalcularResumenReporteUseCase,
            CurrentUserService currentUserService
    ) {
        this.registroProductoRepositoryPort =
                registroProductoRepositoryPort;

        this.registroProductoMapper =
                registroProductoMapper;

        this.actualizarStockUseCase =
                actualizarStockUseCase;

        this.registrarTransaccionUseCase =
                registrarTransaccionUseCase;

        this.recalcularResumenReporteUseCase =
                recalcularResumenReporteUseCase;

        this.currentUserService =
                currentUserService;
    }
    @Transactional
    @Override
    public RegistroProductoResponseDTO editarRegistroProducto(
            int reporteId,
            int registroId,
            RegistroProductoRequestDTO dto
    ) {

        // OBTENER REGISTRO ACTUAL
        Registro actual =
                registroProductoRepositoryPort
                        .findById(registroId);

        Integer usuarioId =
                currentUserService
                        .getCurrentUser()
                        .getId();

        revertirMovimientoAnterior(
                actual,
                usuarioId
        );

        Registro nuevo =
                registroProductoMapper
                        .toDomain(dto);

        Registro actualizado =
                registroProductoRepositoryPort
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

        return registroProductoMapper
                .toDto(actualizado);
    }


    private void revertirMovimientoAnterior(
            Registro actual,
            Integer usuarioId
    ) {

        actualizarStockUseCase.actualizarStock(
                actual.getProduct().getId(),
                actual.getAmount(),
                TipoMovimiento.ENTRADA
        );

        registrarMovimiento(
                usuarioId,
                actual.getProduct().getId(),
                actual.getAmount(),
                TipoMovimiento.ENTRADA
        );
    }



    private void aplicarNuevoMovimiento(
            Registro registro,
            Integer usuarioId
    ) {

        if(registro.getFuenteProducto()
                == FuenteProducto.COMPRA){

            actualizarStockUseCase.actualizarStock(
                    registro.getProduct().getId(),
                    registro.getAmount(),
                    TipoMovimiento.ENTRADA
            );

            registrarMovimiento(
                    usuarioId,
                    registro.getProduct().getId(),
                    registro.getAmount(),
                    TipoMovimiento.ENTRADA
            );
        }

        actualizarStockUseCase.actualizarStock(
                registro.getProduct().getId(),
                registro.getAmount(),
                TipoMovimiento.SALIDA
        );

        registrarMovimiento(
                usuarioId,
                registro.getProduct().getId(),
                registro.getAmount(),
                TipoMovimiento.SALIDA
        );
    }


    private void registrarMovimiento(
            Integer usuarioId,
            Integer productoId,
            BigDecimal amount,
            TipoMovimiento tipo)
    {

        TransaccionRequestDTO dto =new TransaccionRequestDTO();

        dto.setUserId(usuarioId);
        dto.setProductId(productoId);
        dto.setAmount(amount);
        dto.setType(tipo);

        registrarTransaccionUseCase.registrarTransaccion(dto);
    }
}

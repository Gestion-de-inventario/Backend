package com.comedor.backend.application.services;

import com.comedor.backend.application.common.mapper.ProductRecordMapper;
import com.comedor.backend.application.ports.in.ActualizarStockUseCase;
import com.comedor.backend.application.ports.in.EditarRegistroProductoUseCase;
import com.comedor.backend.application.ports.in.RecalcularResumenReporteUseCase;
import com.comedor.backend.application.ports.in.RegistrarTransaccionUseCase;
import com.comedor.backend.application.ports.out.ProductRecordRepositoryPort;
import com.comedor.backend.domain.model.Record;
import com.comedor.backend.domain.model.enums.FuenteProducto;
import com.comedor.backend.domain.model.enums.TipoMovimiento;
import com.comedor.backend.infrastructure.adapters.in.web.dto.request.RegistroProductoRequestDTO;
import com.comedor.backend.infrastructure.adapters.in.web.dto.request.TransaccionRequestDTO;
import com.comedor.backend.infrastructure.adapters.in.web.dto.response.RegistroProductoResponseDTO;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

public class EditarRegistroProductoService implements EditarRegistroProductoUseCase {
    private final ProductRecordRepositoryPort
            productRecordRepositoryPort;

    private final ProductRecordMapper
            productRecordMapper;

    private final ActualizarStockUseCase
            actualizarStockUseCase;

    private final RegistrarTransaccionUseCase
            registrarTransaccionUseCase;

    private final RecalcularResumenReporteUseCase
            recalcularResumenReporteUseCase;

    private final CurrentUserService
            currentUserService;

    public EditarRegistroProductoService(
            ProductRecordRepositoryPort productRecordRepositoryPort,
            ProductRecordMapper productRecordMapper,
            ActualizarStockUseCase actualizarStockUseCase,
            RegistrarTransaccionUseCase registrarTransaccionUseCase,
            RecalcularResumenReporteUseCase recalcularResumenReporteUseCase,
            CurrentUserService currentUserService
    ) {
        this.productRecordRepositoryPort =
                productRecordRepositoryPort;

        this.productRecordMapper =
                productRecordMapper;

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
            Record record,
            Integer usuarioId
    ) {

        if(record.getProductSource()
                == FuenteProducto.COMPRA){

            actualizarStockUseCase.actualizarStock(
                    record.getProduct().getId(),
                    record.getAmount(),
                    TipoMovimiento.ENTRADA
            );

            registrarMovimiento(
                    usuarioId,
                    record.getProduct().getId(),
                    record.getAmount(),
                    TipoMovimiento.ENTRADA
            );
        }

        actualizarStockUseCase.actualizarStock(
                record.getProduct().getId(),
                record.getAmount(),
                TipoMovimiento.SALIDA
        );

        registrarMovimiento(
                usuarioId,
                record.getProduct().getId(),
                record.getAmount(),
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

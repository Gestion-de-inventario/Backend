package com.comedor.backend.application.services;

import com.comedor.backend.application.common.mapper.ProductRecordMapper;
import com.comedor.backend.application.ports.in.*;
import com.comedor.backend.application.ports.out.ProductRecordRepositoryPort;
import com.comedor.backend.domain.exceptions.CantidadProductoInvalida;
import com.comedor.backend.domain.exceptions.PrecioProductoInvalido;
import com.comedor.backend.domain.model.Record;
import com.comedor.backend.domain.model.enums.FuenteProducto;
import com.comedor.backend.domain.model.enums.TipoMovimiento;
import com.comedor.backend.infrastructure.adapters.in.web.dto.request.RegistroProductoRequestDTO;
import com.comedor.backend.infrastructure.adapters.in.web.dto.request.TransaccionRequestDTO;
import com.comedor.backend.infrastructure.adapters.in.web.dto.response.RegistroProductoResponseDTO;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

public class AgregarRegistroProductoService implements AgregarRegistroProductoUseCase {
    private final ProductRecordRepositoryPort productRecordRepositoryPort;
    private final ProductRecordMapper productRecordMapper;
    private final RegistrarTransaccionUseCase registrarTransaccionUseCase;
    private final CurrentUserService currentUserService;
    private final ActualizarStockUseCase actualizarStockUseCase;
    private final RevisarStockUseCase revisarStockUseCase;
    private final RecalcularResumenReporteUseCase recalcularResumenReporteUseCase;

    public AgregarRegistroProductoService(ProductRecordRepositoryPort productRecordRepositoryPort, ProductRecordMapper productRecordMapper, RegistrarTransaccionUseCase registrarTransaccionUseCase, CurrentUserService currentUserService, ActualizarStockUseCase actualizarStockUseCase, RevisarStockUseCase revisarStockUseCase, RecalcularResumenReporteUseCase recalcularResumenReporteUseCase) {
        this.productRecordRepositoryPort = productRecordRepositoryPort;
        this.productRecordMapper = productRecordMapper;
        this.registrarTransaccionUseCase = registrarTransaccionUseCase;
        this.currentUserService = currentUserService;
        this.actualizarStockUseCase = actualizarStockUseCase;
        this.revisarStockUseCase = revisarStockUseCase;
        this.recalcularResumenReporteUseCase = recalcularResumenReporteUseCase;
    }

    @Override
    @Transactional
    public RegistroProductoResponseDTO agregarRegistroProducto( int reporteId, RegistroProductoRequestDTO dto)
        {

            validarDatos(dto);

            Record recordDomain =
                    productRecordMapper.toDomain(dto);

            Record recordCreated =
                    productRecordRepositoryPort
                            .agregarRegistroProducto(
                                    reporteId,
                                    recordDomain
                            );

            Integer usuarioId = currentUserService.getCurrentUser().getId();

            if (recordCreated.getProductSource() == FuenteProducto.COMPRA) {

                registrarMovimiento(
                        usuarioId,
                        dto.getProductoId(),
                        dto.getAmount(),
                        TipoMovimiento.ENTRADA
                );

                actualizarStockUseCase.actualizarStock(
                        dto.getProductoId(),
                        dto.getAmount(),
                        TipoMovimiento.ENTRADA
                );
            }else {

                revisarStockUseCase.validarStockDisponible(
                        dto.getProductoId(),
                        dto.getAmount()
                );
            }

            registrarMovimiento(
                    usuarioId,
                    dto.getProductoId(),
                    dto.getAmount(),
                    TipoMovimiento.SALIDA
            );

            actualizarStockUseCase.actualizarStock(
                    dto.getProductoId(),
                    dto.getAmount(),
                    TipoMovimiento.SALIDA
            );

            recalcularResumenReporteUseCase.recalcular(reporteId);

            return productRecordMapper.toDto(recordCreated);
    }

    private void registrarMovimiento(
            Integer usuarioId,
            Integer productoId,
            BigDecimal amount,
            TipoMovimiento tipo
    ) {
        TransaccionRequestDTO dto = new TransaccionRequestDTO();

        dto.setAmount(amount);
        dto.setProductId(productoId);
        dto.setUserId(usuarioId);
        dto.setType(tipo);

        registrarTransaccionUseCase.registrarTransaccion(dto);
    }

    private void validarDatos(RegistroProductoRequestDTO dto){

        if(dto.getAmount() == null){
            throw new CantidadProductoInvalida(
                    "La cantidad es obligatoria"
            );
        }

        if(dto.getAmount().compareTo(BigDecimal.ZERO) <= 0){
            throw new CantidadProductoInvalida(
                    "La cantidad debe ser mayor a 0"
            );
        }

        if(dto.getUnitPrice() == null){
            throw new PrecioProductoInvalido(
                    "El precio es obligatorio"
            );
        }

        if(dto.getUnitPrice().compareTo(BigDecimal.ZERO) < 0){
            throw new PrecioProductoInvalido(
                    "El precio no puede ser negativo"
            );
        }
    }
}

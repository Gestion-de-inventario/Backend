package com.comedor.backend.application.services;

import com.comedor.backend.application.common.mapper.RegistroProductoMapper;
import com.comedor.backend.application.ports.in.ActualizarStockUseCase;
import com.comedor.backend.application.ports.in.AgregarRegistroProductoUseCase;
import com.comedor.backend.application.ports.in.RegistrarTransaccionUseCase;
import com.comedor.backend.application.ports.in.RevisarStockUseCase;
import com.comedor.backend.application.ports.out.RegistroProductoRepositoryPort;
import com.comedor.backend.domain.exceptions.CantidadProductoInvalida;
import com.comedor.backend.domain.exceptions.PrecioProductoInvalido;
import com.comedor.backend.domain.model.Registro;
import com.comedor.backend.domain.model.enums.FuenteProducto;
import com.comedor.backend.domain.model.enums.TipoMovimiento;
import com.comedor.backend.infrastructure.adapters.in.web.dto.request.RegistroProductoRequestDTO;
import com.comedor.backend.infrastructure.adapters.in.web.dto.request.TransaccionRequestDTO;
import com.comedor.backend.infrastructure.adapters.in.web.dto.response.RegistroProductoResponseDTO;
import jakarta.transaction.Transactional;

import java.math.BigDecimal;

public class AgregarRegistroProductoService implements AgregarRegistroProductoUseCase {
    private final RegistroProductoRepositoryPort registroProductoRepositoryPort;
    private final RegistroProductoMapper registroProductoMapper;
    private final RegistrarTransaccionUseCase registrarTransaccionUseCase;
    private final CurrentUserService currentUserService;
    private final ActualizarStockUseCase actualizarStockUseCase;
    private final RevisarStockUseCase revisarStockUseCase;
    public AgregarRegistroProductoService(RegistroProductoRepositoryPort registroProductoRepositoryPort, RegistroProductoMapper registroProductoMapper, RegistrarTransaccionUseCase registrarTransaccionUseCase, CurrentUserService currentUserService, ActualizarStockUseCase actualizarStockUseCase, RevisarStockUseCase revisarStockUseCase) {
        this.registroProductoRepositoryPort = registroProductoRepositoryPort;
        this.registroProductoMapper = registroProductoMapper;
        this.registrarTransaccionUseCase = registrarTransaccionUseCase;
        this.currentUserService = currentUserService;
        this.actualizarStockUseCase = actualizarStockUseCase;
        this.revisarStockUseCase = revisarStockUseCase;
    }

    @Override
    @Transactional
    public RegistroProductoResponseDTO agregarRegistroProducto( int reporteId, RegistroProductoRequestDTO dto)
        {

            validarDatos(dto);

            Registro registroDomain =
                    registroProductoMapper.toDomain(dto);

            Registro registroCreado =
                    registroProductoRepositoryPort
                            .agregarRegistroProducto(
                                    reporteId,
                                    registroDomain
                            );

            Integer usuarioId = currentUserService.getCurrentUser().getId();

            if (registroCreado.getFuenteProducto() == FuenteProducto.COMPRA) {

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

            return registroProductoMapper.toDto(registroCreado);
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

package com.comedor.backend.application.services;

import com.comedor.backend.application.common.mapper.ProductRecordMapper;
import com.comedor.backend.application.ports.in.*;
import com.comedor.backend.application.ports.out.ProductRecordRepositoryPort;
import com.comedor.backend.domain.exceptions.QuantityProductInvalidException;
import com.comedor.backend.domain.exceptions.InvalidProductPriceException;
import com.comedor.backend.domain.model.enums.MovementType;
import com.comedor.backend.infrastructure.adapters.in.web.dto.request.ProductRecordRequestDTO;
import com.comedor.backend.infrastructure.adapters.in.web.dto.request.TransactionRequestDTO;
import com.comedor.backend.infrastructure.adapters.in.web.dto.response.ProductRecordResponseDTO;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

public class AddRecordProductService implements AddRecordProductUseCase {
    private final ProductRecordRepositoryPort productRecordRepositoryPort;
    private final ProductRecordMapper productRecordMapper;
    private final RegisterTransactionUseCase registerTransactionUseCase;
    private final CurrentUserService currentUserService;
    private final UpdateStockUseCase updateStockUseCase;
    private final CheckStockUseCase checkStockUseCase;
    private final RecalculateSummaryReportUseCase recalcularResumenReporteUseCase;

    public AddRecordProductService(ProductRecordRepositoryPort productRecordRepositoryPort, ProductRecordMapper productRecordMapper, RegisterTransactionUseCase registerTransactionUseCase, CurrentUserService currentUserService, UpdateStockUseCase updateStockUseCase, CheckStockUseCase checkStockUseCase, RecalculateSummaryReportUseCase recalcularResumenReporteUseCase) {
        this.productRecordRepositoryPort = productRecordRepositoryPort;
        this.productRecordMapper = productRecordMapper;
        this.registerTransactionUseCase = registerTransactionUseCase;
        this.currentUserService = currentUserService;
        this.updateStockUseCase = updateStockUseCase;
        this.checkStockUseCase = checkStockUseCase;
        this.recalcularResumenReporteUseCase = recalcularResumenReporteUseCase;
    }

    @Override
    @Transactional
    public ProductRecordResponseDTO agregarRegistroProducto(int reporteId, ProductRecordRequestDTO dto)
        {

            //DEPRECADO
            return new ProductRecordResponseDTO();
    }

    private void registrarMovimiento(
            Integer usuarioId,
            Integer productoId,
            BigDecimal amount,
            MovementType tipo
    ) {
        TransactionRequestDTO dto = new TransactionRequestDTO();

        dto.setAmount(amount);
        dto.setProductId(productoId);
        dto.setUserId(usuarioId);
        dto.setType(tipo);

        registerTransactionUseCase.registrarTransaccion(dto);
    }

    private void validarDatos(ProductRecordRequestDTO dto){

        if(dto.getAmount() == null){
            throw new QuantityProductInvalidException(
                    "La cantidad es obligatoria"
            );
        }

        if(dto.getAmount().compareTo(BigDecimal.ZERO) <= 0){
            throw new QuantityProductInvalidException(
                    "La cantidad debe ser mayor a 0"
            );
        }

        if(dto.getUnitPrice() == null){
            throw new InvalidProductPriceException(
                    "El precio es obligatorio"
            );
        }

        if(dto.getUnitPrice().compareTo(BigDecimal.ZERO) < 0){
            throw new InvalidProductPriceException(
                    "El precio no puede ser negativo"
            );
        }
    }
}

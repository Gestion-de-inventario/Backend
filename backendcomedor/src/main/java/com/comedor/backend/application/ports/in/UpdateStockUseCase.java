package com.comedor.backend.application.ports.in;

import com.comedor.backend.domain.model.enums.MovementType;

import java.math.BigDecimal;

public interface UpdateStockUseCase {
    void actualizarStock(
            int productoId,
            BigDecimal cantidad,
            MovementType movementType
    );
}

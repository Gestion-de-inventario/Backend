package com.comedor.backend.application.ports.in;

import com.comedor.backend.domain.model.enums.TipoMovimiento;

import java.math.BigDecimal;

public interface ActualizarStockUseCase {
    void actualizarStock(
            int productoId,
            BigDecimal cantidad,
            TipoMovimiento tipoMovimiento
    );
}

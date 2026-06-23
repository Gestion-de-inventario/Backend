package com.comedor.backend.application.ports.in;

import java.math.BigDecimal;

public interface CheckStockUseCase {
    void validarStockDisponible(
            int productoId,
            BigDecimal cantidad
    );
}
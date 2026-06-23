package com.comedor.backend.application.services;

import com.comedor.backend.application.ports.in.CheckStockUseCase;
import com.comedor.backend.application.ports.out.ProductRepositoryPort;
import com.comedor.backend.domain.model.Product;

import java.math.BigDecimal;

public class CheckStockService implements CheckStockUseCase {
    private final ProductRepositoryPort productRepositoryPort;

    public CheckStockService(ProductRepositoryPort productRepositoryPort) {
        this.productRepositoryPort = productRepositoryPort;
    }

    @Override
    public void validarStockDisponible(int productoId,BigDecimal cantidad)
    {
        Product product = productRepositoryPort.getProductoById(productoId);
    }
}

package com.comedor.backend.application.services;

import com.comedor.backend.application.ports.in.RevisarStockUseCase;
import com.comedor.backend.application.ports.out.ProductRepositoryPort;
import com.comedor.backend.domain.exceptions.StockInsuficienteException;
import com.comedor.backend.domain.model.Product;

import java.math.BigDecimal;

public class RevisarStockService implements RevisarStockUseCase {
    private final ProductRepositoryPort productRepositoryPort;

    public RevisarStockService(ProductRepositoryPort productRepositoryPort) {
        this.productRepositoryPort = productRepositoryPort;
    }

    @Override
    public void validarStockDisponible(int productoId,BigDecimal cantidad)
    {
        Product product = productRepositoryPort.getProductoById(productoId);

        if(product.getStock().compareTo(cantidad) < 0)
        {
            throw new StockInsuficienteException(
                    "Stock insuficiente para el producto: "
                            + product.getName()
            );
        }
    }
}

package com.comedor.backend.application.services;

import com.comedor.backend.application.ports.in.RevisarStockUseCase;
import com.comedor.backend.application.ports.out.ProductoRepositoryPort;
import com.comedor.backend.domain.exceptions.StockInsuficienteException;
import com.comedor.backend.domain.model.Producto;

import java.math.BigDecimal;

public class RevisarStockService implements RevisarStockUseCase {
    private final ProductoRepositoryPort productoRepositoryPort;

    public RevisarStockService(ProductoRepositoryPort productoRepositoryPort) {
        this.productoRepositoryPort = productoRepositoryPort;
    }

    @Override
    public void validarStockDisponible(int productoId,BigDecimal cantidad)
    {
        Producto producto =productoRepositoryPort.getProductoById(productoId);

        if(producto.getStock().compareTo(cantidad) < 0)
        {
            throw new StockInsuficienteException(
                    "Stock insuficiente para el producto: "
                            + producto.getName()
            );
        }
    }
}

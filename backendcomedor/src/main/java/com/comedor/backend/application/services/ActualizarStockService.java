package com.comedor.backend.application.services;

import com.comedor.backend.application.ports.in.ActualizarStockUseCase;
import com.comedor.backend.application.ports.out.ProductoRepositoryPort;
import com.comedor.backend.domain.model.Producto;
import com.comedor.backend.domain.model.enums.TipoMovimiento;

import java.math.BigDecimal;

public class ActualizarStockService implements ActualizarStockUseCase {

    private final ProductoRepositoryPort productoRepositoryPort;

    public ActualizarStockService(ProductoRepositoryPort productoRepositoryPort) {
        this.productoRepositoryPort = productoRepositoryPort;
    }

    @Override
    public void actualizarStock(int productoId, BigDecimal cantidad, TipoMovimiento tipoMovimiento) {
        Producto producto =
                productoRepositoryPort
                        .getProductoById(productoId);

        if(tipoMovimiento == TipoMovimiento.ENTRADA)
        {
            producto.setStock(
                    producto.getStock()
                            .add(cantidad)
            );
        }
        else
        {
            producto.setStock(
                    producto.getStock()
                            .subtract(cantidad)
            );
        }
        productoRepositoryPort.updateStock(producto);
    }
}

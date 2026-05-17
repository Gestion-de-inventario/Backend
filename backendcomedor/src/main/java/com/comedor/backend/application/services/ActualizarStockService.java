package com.comedor.backend.application.services;

import com.comedor.backend.application.ports.in.ActualizarStockUseCase;
import com.comedor.backend.application.ports.out.ProductRepositoryPort;
import com.comedor.backend.domain.model.Product;
import com.comedor.backend.domain.model.enums.TipoMovimiento;

import java.math.BigDecimal;

public class ActualizarStockService implements ActualizarStockUseCase {

    private final ProductRepositoryPort productRepositoryPort;

    public ActualizarStockService(ProductRepositoryPort productRepositoryPort) {
        this.productRepositoryPort = productRepositoryPort;
    }

    @Override
    public void actualizarStock(int productoId, BigDecimal cantidad, TipoMovimiento tipoMovimiento) {
        Product product =
                productRepositoryPort
                        .getProductoById(productoId);

        if(tipoMovimiento == TipoMovimiento.ENTRADA)
        {
            product.setStock(
                    product.getStock()
                            .add(cantidad)
            );
        }
        else
        {
            product.setStock(
                    product.getStock()
                            .subtract(cantidad)
            );
        }
        productRepositoryPort.updateStock(product);
    }
}

package com.comedor.backend.application.services;

import com.comedor.backend.application.ports.in.UpdateStockUseCase;
import com.comedor.backend.application.ports.out.ProductRepositoryPort;
import com.comedor.backend.domain.model.Product;
import com.comedor.backend.domain.model.enums.MovementType;

import java.math.BigDecimal;

public class UpdateStockService implements UpdateStockUseCase {

    private final ProductRepositoryPort productRepositoryPort;

    public UpdateStockService(ProductRepositoryPort productRepositoryPort) {
        this.productRepositoryPort = productRepositoryPort;
    }

    @Override
    public void actualizarStock(int productoId, BigDecimal cantidad, MovementType movementType) {
        Product product =
                productRepositoryPort
                        .getProductoById(productoId);

        if(movementType == MovementType.ENTRADA)
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

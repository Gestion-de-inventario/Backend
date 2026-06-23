package com.comedor.backend.application.services;

import com.comedor.backend.application.ports.in.GetStockAlertsUseCase;
import com.comedor.backend.application.ports.out.ProductRepositoryPort;
import com.comedor.backend.domain.model.Product;

import java.util.List;

public class GetStockAlertsService implements GetStockAlertsUseCase {

    private final ProductRepositoryPort productRepositoryPort;

    public GetStockAlertsService(ProductRepositoryPort productRepositoryPort) {
        this.productRepositoryPort = productRepositoryPort;
    }

    @Override
    public List<Product> obtenerProductosBajoStock() {
        return productRepositoryPort.getProductosBajoStockMinimo();
    }
}

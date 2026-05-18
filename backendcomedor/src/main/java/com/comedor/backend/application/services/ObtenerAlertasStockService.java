package com.comedor.backend.application.services;

import com.comedor.backend.application.ports.in.ObtenerAlertasStockUseCase;
import com.comedor.backend.application.ports.out.ProductRepositoryPort;
import com.comedor.backend.domain.model.Product;

import java.util.List;

public class ObtenerAlertasStockService implements ObtenerAlertasStockUseCase {

    private final ProductRepositoryPort productRepositoryPort;

    public ObtenerAlertasStockService(ProductRepositoryPort productRepositoryPort) {
        this.productRepositoryPort = productRepositoryPort;
    }

    @Override
    public List<Product> obtenerProductosBajoStock() {
        return productRepositoryPort.getProductosBajoStockMinimo();
    }
}

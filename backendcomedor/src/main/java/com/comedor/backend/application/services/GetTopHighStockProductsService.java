package com.comedor.backend.application.services;

import com.comedor.backend.application.ports.in.GetTopHighStockProductsUseCase;
import com.comedor.backend.application.ports.out.ProductRepositoryPort;
import com.comedor.backend.domain.model.Product;

import java.util.List;

public class GetTopHighStockProductsService implements GetTopHighStockProductsUseCase {
    private final ProductRepositoryPort productRepositoryPort;

    public GetTopHighStockProductsService(ProductRepositoryPort productRepositoryPort) {
        this.productRepositoryPort = productRepositoryPort;
    }

    @Override
    public List<Product> getTopHighStock() {
        return productRepositoryPort.getTop5HighStock();
    }
}

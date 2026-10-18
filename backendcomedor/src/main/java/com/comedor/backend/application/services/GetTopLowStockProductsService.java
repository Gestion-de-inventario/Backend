package com.comedor.backend.application.services;

import com.comedor.backend.application.ports.in.GetTopLowStockProductsUseCase;
import com.comedor.backend.application.ports.out.ProductRepositoryPort;
import com.comedor.backend.domain.model.Product;

import java.util.List;

public class GetTopLowStockProductsService implements GetTopLowStockProductsUseCase {
    private final ProductRepositoryPort productRepositoryPort;

    public GetTopLowStockProductsService(ProductRepositoryPort productRepositoryPort) {
        this.productRepositoryPort = productRepositoryPort;
    }
    @Override
    public List<Product> getTopLowStock() {
        return productRepositoryPort.getTop5LowStock();
    }
}

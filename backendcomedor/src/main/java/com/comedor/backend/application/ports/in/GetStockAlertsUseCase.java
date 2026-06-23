package com.comedor.backend.application.ports.in;

import com.comedor.backend.domain.model.Product;

import java.util.List;

public interface GetStockAlertsUseCase {
    List<Product> obtenerProductosBajoStock();
}

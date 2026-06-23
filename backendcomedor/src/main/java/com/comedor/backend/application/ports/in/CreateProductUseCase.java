package com.comedor.backend.application.ports.in;

import com.comedor.backend.infrastructure.adapters.in.web.dto.request.ProductRequestDTO;
import com.comedor.backend.infrastructure.adapters.in.web.dto.response.ProductResponseDTO;

public interface CreateProductUseCase {
    ProductResponseDTO crearProducto(ProductRequestDTO productRequestDTO);
}

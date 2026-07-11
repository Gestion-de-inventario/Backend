package com.comedor.backend.application.ports.in;

import com.comedor.backend.domain.model.Product;
import com.comedor.backend.infrastructure.adapters.in.web.dto.request.EditProductRequestDTO;
import com.comedor.backend.infrastructure.adapters.in.web.dto.response.ProductResponseDTO;

public interface EditProductUseCase {
    ProductResponseDTO editar(Integer id, EditProductRequestDTO productoRequestDTO);
}

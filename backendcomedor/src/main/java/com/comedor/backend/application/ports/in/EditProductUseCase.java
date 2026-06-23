package com.comedor.backend.application.ports.in;

import com.comedor.backend.domain.model.Product;
import com.comedor.backend.infrastructure.adapters.in.web.dto.request.EditProductRequestDTO;

public interface EditProductUseCase {
    Product editar(int id, EditProductRequestDTO productoRequestDTO);
}

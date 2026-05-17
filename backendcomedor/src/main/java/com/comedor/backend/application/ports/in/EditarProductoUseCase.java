package com.comedor.backend.application.ports.in;

import com.comedor.backend.domain.model.Product;
import com.comedor.backend.infrastructure.adapters.in.web.dto.request.EditarProductoRequestDTO;

public interface EditarProductoUseCase {
    Product editar(int id, EditarProductoRequestDTO productoRequestDTO);
}

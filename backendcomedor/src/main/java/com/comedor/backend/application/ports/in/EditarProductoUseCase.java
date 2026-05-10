package com.comedor.backend.application.ports.in;

import com.comedor.backend.domain.model.Producto;
import com.comedor.backend.infrastructure.adapters.in.web.dto.request.EditarProductoRequestDTO;

public interface EditarProductoUseCase {
    Producto editar(int id, EditarProductoRequestDTO productoRequestDTO);
}

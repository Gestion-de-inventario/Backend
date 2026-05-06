package com.comedor.backend.application.ports.in;

import com.comedor.backend.infrastructure.adapters.in.web.dto.response.ProductoResponseDTO;

public interface DesactivarProductoUseCase {
    ProductoResponseDTO desactivarProductoPorId(int id);
}

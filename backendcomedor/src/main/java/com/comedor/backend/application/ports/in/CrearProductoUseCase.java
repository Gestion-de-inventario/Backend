package com.comedor.backend.application.ports.in;

import com.comedor.backend.domain.model.Producto;
import com.comedor.backend.infrastructure.adapters.in.web.dto.request.ProductoRequestDTO;
import com.comedor.backend.infrastructure.adapters.in.web.dto.response.ProductoResponseDTO;

public interface CrearProductoUseCase {
    ProductoResponseDTO crearProducto(ProductoRequestDTO productoRequestDTO);
}

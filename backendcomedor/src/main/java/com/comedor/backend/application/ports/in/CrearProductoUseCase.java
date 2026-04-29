package com.comedor.backend.application.ports.in;

import com.comedor.backend.domain.model.Producto;

public interface CrearProductoUseCase {
    void crearProducto(Producto producto);
}

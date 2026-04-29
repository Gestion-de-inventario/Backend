package com.comedor.backend.application.ports.out;

import com.comedor.backend.domain.model.Producto;

public interface ProductoRepositoryPort {
    void guardar(Producto producto);
}

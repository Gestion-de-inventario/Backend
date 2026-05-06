package com.comedor.backend.application.services;

import com.comedor.backend.application.common.mapper.ProductoMapper;
import com.comedor.backend.application.ports.in.ActivarProductoUseCase;
import com.comedor.backend.application.ports.out.ProductoRepositoryPort;
import com.comedor.backend.infrastructure.adapters.in.web.dto.response.ProductoResponseDTO;
import com.comedor.backend.infrastructure.adapters.out.persistence.ProductoRepositoryAdapter;

public class ActivarProductoService implements ActivarProductoUseCase {
    private final ProductoRepositoryPort productoRepositoryPort;
    private final ProductoMapper productoMapper;

    public ActivarProductoService(ProductoRepositoryPort productoRepositoryPort, ProductoMapper productoMapper) {
        this.productoRepositoryPort = productoRepositoryPort;
        this.productoMapper = productoMapper;
    }

    @Override
    public ProductoResponseDTO activarProductoPorId(int id) {
        return productoMapper.productoResponseDTO(productoRepositoryPort.activateById(id));
    }
}

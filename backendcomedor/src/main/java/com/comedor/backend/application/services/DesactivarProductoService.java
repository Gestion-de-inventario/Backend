package com.comedor.backend.application.services;

import com.comedor.backend.application.common.mapper.ProductoMapper;
import com.comedor.backend.application.ports.in.DesactivarProductoUseCase;
import com.comedor.backend.application.ports.out.ProductoRepositoryPort;
import com.comedor.backend.infrastructure.adapters.in.web.dto.response.ProductoResponseDTO;

public class DesactivarProductoService implements DesactivarProductoUseCase {
    private final ProductoRepositoryPort productoRepositoryPort;
    private final ProductoMapper productoMapper;


    public DesactivarProductoService(ProductoRepositoryPort productoRepositoryPort, ProductoMapper productoMapper) {
        this.productoRepositoryPort = productoRepositoryPort;
        this.productoMapper = productoMapper;
    }

    @Override
    public ProductoResponseDTO desactivarProductoPorId(int id) {
        return productoMapper.productoResponseDTO(productoRepositoryPort.deactivateById(id));
    }
}

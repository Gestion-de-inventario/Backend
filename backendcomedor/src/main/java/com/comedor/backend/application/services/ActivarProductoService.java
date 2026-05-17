package com.comedor.backend.application.services;

import com.comedor.backend.application.common.mapper.ProductMapper;
import com.comedor.backend.application.ports.in.ActivarProductoUseCase;
import com.comedor.backend.application.ports.out.ProductRepositoryPort;
import com.comedor.backend.infrastructure.adapters.in.web.dto.response.ProductoResponseDTO;

public class ActivarProductoService implements ActivarProductoUseCase {
    private final ProductRepositoryPort productRepositoryPort;
    private final ProductMapper productMapper;

    public ActivarProductoService(ProductRepositoryPort productRepositoryPort, ProductMapper productMapper) {
        this.productRepositoryPort = productRepositoryPort;
        this.productMapper = productMapper;
    }

    @Override
    public ProductoResponseDTO activarProductoPorId(int id) {
        return productMapper.productoResponseDTO(productRepositoryPort.activateById(id));
    }
}

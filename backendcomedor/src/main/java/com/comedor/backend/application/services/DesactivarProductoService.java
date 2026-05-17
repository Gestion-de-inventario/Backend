package com.comedor.backend.application.services;

import com.comedor.backend.application.common.mapper.ProductMapper;
import com.comedor.backend.application.ports.in.DesactivarProductoUseCase;
import com.comedor.backend.application.ports.out.ProductRepositoryPort;
import com.comedor.backend.infrastructure.adapters.in.web.dto.response.ProductoResponseDTO;

public class DesactivarProductoService implements DesactivarProductoUseCase {
    private final ProductRepositoryPort productRepositoryPort;
    private final ProductMapper productMapper;


    public DesactivarProductoService(ProductRepositoryPort productRepositoryPort, ProductMapper productMapper) {
        this.productRepositoryPort = productRepositoryPort;
        this.productMapper = productMapper;
    }

    @Override
    public ProductoResponseDTO desactivarProductoPorId(int id) {
        return productMapper.productoResponseDTO(productRepositoryPort.deactivateById(id));
    }
}

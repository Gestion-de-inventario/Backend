package com.comedor.backend.application.services;

import com.comedor.backend.application.common.mapper.ProductMapper;
import com.comedor.backend.application.ports.in.ListProductsByStatusUseCase;
import com.comedor.backend.application.ports.out.ProductRepositoryPort;
import com.comedor.backend.domain.model.enums.Status;
import com.comedor.backend.infrastructure.adapters.in.web.dto.response.ProductResponseDTO;

import java.util.List;

public class ListProductsByStatusService implements ListProductsByStatusUseCase {

    private final ProductRepositoryPort productRepositoryPort;
    private final ProductMapper productMapper;

    public ListProductsByStatusService(ProductRepositoryPort productRepositoryPort, ProductMapper productMapper) {
        this.productRepositoryPort = productRepositoryPort;
        this.productMapper = productMapper;
    }

    @Override
    public List<ProductResponseDTO> listarProductosPorEstado(Status status) {
        return productMapper.toListProductoResponseDTO(productRepositoryPort.getProductosByStatus(status));
    }
}

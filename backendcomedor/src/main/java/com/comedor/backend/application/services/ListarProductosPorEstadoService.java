package com.comedor.backend.application.services;

import com.comedor.backend.application.common.mapper.ProductMapper;
import com.comedor.backend.application.ports.in.ListarProductosPorEstadoUseCase;
import com.comedor.backend.application.ports.out.ProductRepositoryPort;
import com.comedor.backend.domain.model.enums.Estado;
import com.comedor.backend.infrastructure.adapters.in.web.dto.response.ProductoResponseDTO;

import java.util.List;

public class ListarProductosPorEstadoService implements ListarProductosPorEstadoUseCase {

    private final ProductRepositoryPort productRepositoryPort;
    private final ProductMapper productMapper;

    public ListarProductosPorEstadoService(ProductRepositoryPort productRepositoryPort, ProductMapper productMapper) {
        this.productRepositoryPort = productRepositoryPort;
        this.productMapper = productMapper;
    }

    @Override
    public List<ProductoResponseDTO> listarProductosPorEstado(Estado estado) {
        return productMapper.toListProductoResponseDTO(productRepositoryPort.getProductosByStatus(estado));
    }
}

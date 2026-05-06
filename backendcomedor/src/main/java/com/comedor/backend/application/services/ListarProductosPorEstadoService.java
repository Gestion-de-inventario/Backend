package com.comedor.backend.application.services;

import com.comedor.backend.application.common.mapper.ProductoMapper;
import com.comedor.backend.application.ports.in.ListarProductosPorEstadoUseCase;
import com.comedor.backend.application.ports.out.ProductoRepositoryPort;
import com.comedor.backend.domain.model.enums.Estado;
import com.comedor.backend.infrastructure.adapters.in.web.dto.response.ProductoResponseDTO;

import java.util.List;

public class ListarProductosPorEstadoService implements ListarProductosPorEstadoUseCase {

    private final ProductoRepositoryPort productoRepositoryPort;
    private final ProductoMapper productoMapper;

    public ListarProductosPorEstadoService(ProductoRepositoryPort productoRepositoryPort, ProductoMapper productoMapper) {
        this.productoRepositoryPort = productoRepositoryPort;
        this.productoMapper = productoMapper;
    }

    @Override
    public List<ProductoResponseDTO> listarProductosPorEstado(Estado estado) {
        return productoMapper.toListProductoResponseDTO(productoRepositoryPort.getProductosByStatus(estado));
    }
}

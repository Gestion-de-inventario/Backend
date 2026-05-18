package com.comedor.backend.application.services;

import com.comedor.backend.application.common.mapper.ProductMapper;
import com.comedor.backend.application.ports.in.ActivarProductoUseCase;
import com.comedor.backend.application.ports.in.RegistrarModificacionUseCase;
import com.comedor.backend.application.ports.out.ProductRepositoryPort;
import com.comedor.backend.infrastructure.adapters.in.web.dto.request.ModificationsRequestDTO;
import com.comedor.backend.infrastructure.adapters.in.web.dto.response.ProductoResponseDTO;

public class ActivarProductoService implements ActivarProductoUseCase {
    private final ProductRepositoryPort productRepositoryPort;
    private final ProductMapper productMapper;
    private final RegistrarModificacionUseCase registrarModificacionUseCase;

    public ActivarProductoService(ProductRepositoryPort productRepositoryPort, ProductMapper productMapper, RegistrarModificacionUseCase registrarModificacionUseCase) {
        this.productRepositoryPort = productRepositoryPort;
        this.productMapper = productMapper;
        this.registrarModificacionUseCase = registrarModificacionUseCase;
    }

    @Override
    public ProductoResponseDTO activarProductoPorId(int id) {
        ProductoResponseDTO resultado = productMapper.productoResponseDTO(productRepositoryPort.activateById(id));

        registrarModificacionUseCase.registrar(new ModificationsRequestDTO(
                "Producto",
                "status",
                "INACTIVO",
                "ACTIVO"
        ));

        return resultado;
    }
}

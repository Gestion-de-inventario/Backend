package com.comedor.backend.application.services;

import com.comedor.backend.application.common.mapper.ProductMapper;
import com.comedor.backend.application.ports.in.DesactivarProductoUseCase;
import com.comedor.backend.application.ports.in.RegistrarModificacionUseCase;
import com.comedor.backend.application.ports.out.ProductRepositoryPort;
import com.comedor.backend.infrastructure.adapters.in.web.dto.request.ModificationsRequestDTO;
import com.comedor.backend.infrastructure.adapters.in.web.dto.response.ProductoResponseDTO;

public class DesactivarProductoService implements DesactivarProductoUseCase {
    private final ProductRepositoryPort productRepositoryPort;
    private final ProductMapper productMapper;
    private final RegistrarModificacionUseCase registrarModificacionUseCase;


    public DesactivarProductoService(ProductRepositoryPort productRepositoryPort, ProductMapper productMapper, RegistrarModificacionUseCase registrarModificacionUseCase) {
        this.productRepositoryPort = productRepositoryPort;
        this.productMapper = productMapper;
        this.registrarModificacionUseCase = registrarModificacionUseCase;
    }

    @Override
    public ProductoResponseDTO desactivarProductoPorId(int id) {
        ProductoResponseDTO resultado = productMapper.productoResponseDTO(productRepositoryPort.deactivateById(id));
        registrarModificacionUseCase.registrar(new ModificationsRequestDTO(
                "Producto",
                "status",
                "ACTIVO",
                "INACTIVO"
        ));
        return resultado;
    }
}

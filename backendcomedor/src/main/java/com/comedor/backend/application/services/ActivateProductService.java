package com.comedor.backend.application.services;

import com.comedor.backend.application.common.mapper.ProductMapper;
import com.comedor.backend.application.ports.in.ActivateProductUseCase;
import com.comedor.backend.application.ports.in.RegisterModificationUseCase;
import com.comedor.backend.application.ports.out.ProductRepositoryPort;
import com.comedor.backend.infrastructure.adapters.in.web.dto.request.ModificationsRequestDTO;
import com.comedor.backend.infrastructure.adapters.in.web.dto.response.ProductResponseDTO;

public class ActivateProductService implements ActivateProductUseCase {
    private final ProductRepositoryPort productRepositoryPort;
    private final ProductMapper productMapper;
    private final RegisterModificationUseCase registerModificationUseCase;

    public ActivateProductService(ProductRepositoryPort productRepositoryPort, ProductMapper productMapper, RegisterModificationUseCase registerModificationUseCase) {
        this.productRepositoryPort = productRepositoryPort;
        this.productMapper = productMapper;
        this.registerModificationUseCase = registerModificationUseCase;
    }

    @Override
    public ProductResponseDTO activarProductoPorId(int id) {
        ProductResponseDTO resultado = productMapper.productoResponseDTO(productRepositoryPort.activateById(id));

        registerModificationUseCase.registrar(new ModificationsRequestDTO(
                "Producto",
                "status",
                "INACTIVO",
                "ACTIVO"
        ));

        return resultado;
    }
}

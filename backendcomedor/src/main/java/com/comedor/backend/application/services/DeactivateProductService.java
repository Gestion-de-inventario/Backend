package com.comedor.backend.application.services;

import com.comedor.backend.application.common.mapper.ProductMapper;
import com.comedor.backend.application.ports.in.DeactivateProductUseCase;
import com.comedor.backend.application.ports.in.RegisterAuditUseCase;
import com.comedor.backend.application.ports.out.ProductRepositoryPort;
import com.comedor.backend.domain.model.enums.AuditAction;
import com.comedor.backend.infrastructure.adapters.in.web.dto.request.AuditRequestDTO;
import com.comedor.backend.infrastructure.adapters.in.web.dto.response.ProductResponseDTO;

import java.util.Map;

public class DeactivateProductService implements DeactivateProductUseCase {
    private final ProductRepositoryPort productRepositoryPort;
    private final ProductMapper productMapper;
    private final RegisterAuditUseCase registerAuditUseCase;


    public DeactivateProductService(ProductRepositoryPort productRepositoryPort, ProductMapper productMapper,  RegisterAuditUseCase registerAuditUseCase) {
        this.productRepositoryPort = productRepositoryPort;
        this.productMapper = productMapper;
        this.registerAuditUseCase = registerAuditUseCase;
    }

    @Override
    public ProductResponseDTO desactivarProductoPorId(int id) {
        ProductResponseDTO resultado = productMapper.productoResponseDTO(productRepositoryPort.deactivateById(id));
        registerAuditUseCase.registrar(
                new AuditRequestDTO(
                        "Producto",
                        id,
                        resultado.getName(),
                        AuditAction.MODIFICACION,
                        Map.of(
                                "attribute", "estado",
                                "previousValue", "ACTIVO",
                                "newValue", "INACTIVO"
                        )
                )
        );
        return resultado;
    }
}

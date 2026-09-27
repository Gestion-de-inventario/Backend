package com.comedor.backend.application.services;

import com.comedor.backend.application.common.mapper.CategoryMapper;
import com.comedor.backend.application.ports.in.DeactivateCategoryUseCase;
import com.comedor.backend.application.ports.in.RegisterAuditUseCase;
import com.comedor.backend.application.ports.out.CategoryRepositoryPort;
import com.comedor.backend.domain.model.enums.AuditAction;
import com.comedor.backend.infrastructure.adapters.in.web.dto.request.AuditRequestDTO;
import com.comedor.backend.infrastructure.adapters.in.web.dto.response.CategoryResponseDTO;

import java.util.Map;

public class DeactivateCategoryService implements DeactivateCategoryUseCase {

    private final CategoryRepositoryPort categoryRepositoryPort;
    private final CategoryMapper categoryMapper;
    private final RegisterAuditUseCase registerAuditUseCase;
    public DeactivateCategoryService(CategoryRepositoryPort repository, CategoryMapper mapper, RegisterAuditUseCase registerAuditUseCase) {
        this.categoryRepositoryPort = repository;
        this.categoryMapper = mapper;
        this.registerAuditUseCase = registerAuditUseCase;
    }


    @Override
    public CategoryResponseDTO desactivarCategoriaPorId(int id) {
        CategoryResponseDTO resultado = categoryMapper.toCategoriaResponseDTO(categoryRepositoryPort.deactivateById(id));
        registerAuditUseCase.registrar(
                new AuditRequestDTO(
                        "Categoria",
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

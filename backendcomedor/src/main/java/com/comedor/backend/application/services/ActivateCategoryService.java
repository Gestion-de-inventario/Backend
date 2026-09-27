package com.comedor.backend.application.services;

import com.comedor.backend.application.common.mapper.CategoryMapper;
import com.comedor.backend.application.ports.in.ActivateCategoryUseCase;

import com.comedor.backend.application.ports.in.RegisterAuditUseCase;
import com.comedor.backend.application.ports.out.CategoryRepositoryPort;

import com.comedor.backend.domain.model.enums.AuditAction;
import com.comedor.backend.infrastructure.adapters.in.web.dto.request.AuditRequestDTO;
import com.comedor.backend.infrastructure.adapters.in.web.dto.response.CategoryResponseDTO;

import java.util.Map;

public class ActivateCategoryService implements ActivateCategoryUseCase {
    private final CategoryRepositoryPort categoryRepositoryPort;
    private final CategoryMapper categoryMapper;
    private final RegisterAuditUseCase registerAuditUseCase;

    public ActivateCategoryService(CategoryRepositoryPort categoryRepositoryPort, CategoryMapper categoryMapper, RegisterAuditUseCase registerAuditUseCase) {
        this.categoryRepositoryPort = categoryRepositoryPort;
        this.categoryMapper = categoryMapper;
        this.registerAuditUseCase = registerAuditUseCase;
    }


    @Override
    public CategoryResponseDTO activarCategoriaPorId(int id) {
        CategoryResponseDTO categoryResponseDTO = categoryMapper.toCategoriaResponseDTO(categoryRepositoryPort.activateById(id));
        registerAuditUseCase.registrar(
                new AuditRequestDTO(
                        "Categoria",
                        id,
                        categoryResponseDTO.getName(),
                        AuditAction.MODIFICACION,
                        Map.of(
                                "attribute", "estado",
                                "previousValue", "INACTIVO",
                                "newValue", "ACTIVO"
                        )
                )
        );

        return categoryResponseDTO;
    }
}

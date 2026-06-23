package com.comedor.backend.application.services;

import com.comedor.backend.application.common.mapper.CategoryMapper;
import com.comedor.backend.application.ports.in.ActivateCategoryUseCase;
import com.comedor.backend.application.ports.in.RegisterModificationUseCase;
import com.comedor.backend.application.ports.out.CategoryRepositoryPort;
import com.comedor.backend.infrastructure.adapters.in.web.dto.request.ModificationsRequestDTO;
import com.comedor.backend.infrastructure.adapters.in.web.dto.response.CategoryResponseDTO;

public class ActivateCategoryService implements ActivateCategoryUseCase {
    private final CategoryRepositoryPort categoryRepositoryPort;
    private final CategoryMapper categoryMapper;
    private final RegisterModificationUseCase registerModificationUseCase;

    public ActivateCategoryService(CategoryRepositoryPort categoryRepositoryPort, CategoryMapper categoryMapper, RegisterModificationUseCase registerModificationUseCase) {
        this.categoryRepositoryPort = categoryRepositoryPort;
        this.categoryMapper = categoryMapper;
        this.registerModificationUseCase = registerModificationUseCase;
    }


    @Override
    public CategoryResponseDTO activarCategoriaPorId(int id) {
        CategoryResponseDTO categoryResponseDTO = categoryMapper.toCategoriaResponseDTO(categoryRepositoryPort.activateById(id));

        registerModificationUseCase.registrar(new ModificationsRequestDTO(
                "Categoria",
                "status",
                "INACTIVO",
                "ACTIVO"
        ));

        return categoryResponseDTO;
    }
}

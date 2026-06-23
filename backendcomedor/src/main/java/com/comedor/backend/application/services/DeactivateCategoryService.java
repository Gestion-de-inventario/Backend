package com.comedor.backend.application.services;

import com.comedor.backend.application.common.mapper.CategoryMapper;
import com.comedor.backend.application.ports.in.DeactivateCategoryUseCase;
import com.comedor.backend.application.ports.in.RegisterModificationUseCase;
import com.comedor.backend.application.ports.out.CategoryRepositoryPort;
import com.comedor.backend.infrastructure.adapters.in.web.dto.request.ModificationsRequestDTO;
import com.comedor.backend.infrastructure.adapters.in.web.dto.response.CategoryResponseDTO;

public class DeactivateCategoryService implements DeactivateCategoryUseCase {

    private final CategoryRepositoryPort categoryRepositoryPort;
    private final CategoryMapper categoryMapper;
    private final RegisterModificationUseCase registerModificationUseCase;
    public DeactivateCategoryService(CategoryRepositoryPort repository, CategoryMapper mapper, RegisterModificationUseCase registerModificationUseCase) {
        this.categoryRepositoryPort = repository;
        this.categoryMapper = mapper;
        this.registerModificationUseCase = registerModificationUseCase;
    }


    @Override
    public CategoryResponseDTO desactivarCategoriaPorId(int id) {
        CategoryResponseDTO resultado = categoryMapper.toCategoriaResponseDTO(categoryRepositoryPort.deactivateById(id));
        registerModificationUseCase.registrar(new ModificationsRequestDTO(
                "Categoria",
                "status",
                "ACTIVO",
                "INACTIVO"
        ));
        return resultado;
    }
}

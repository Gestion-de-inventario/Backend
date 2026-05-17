package com.comedor.backend.application.services;

import com.comedor.backend.application.common.mapper.CategoryMapper;
import com.comedor.backend.application.ports.in.ActivarCategoriaUseCase;
import com.comedor.backend.application.ports.out.CategoryRepositoryPort;
import com.comedor.backend.infrastructure.adapters.in.web.dto.response.CategoriaResponseDTO;

public class ActivarCategoriaService implements ActivarCategoriaUseCase {
    private final CategoryRepositoryPort categoryRepositoryPort;
    private final CategoryMapper categoryMapper;

    public ActivarCategoriaService(CategoryRepositoryPort categoryRepositoryPort, CategoryMapper categoryMapper) {
        this.categoryRepositoryPort = categoryRepositoryPort;
        this.categoryMapper = categoryMapper;
    }


    @Override
    public CategoriaResponseDTO activarCategoriaPorId(int id) {
        return categoryMapper.toCategoriaResponseDTO(categoryRepositoryPort.activateById(id));
    }
}

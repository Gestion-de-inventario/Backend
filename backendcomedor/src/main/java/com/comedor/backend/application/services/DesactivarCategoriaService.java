package com.comedor.backend.application.services;

import com.comedor.backend.application.common.mapper.CategoryMapper;
import com.comedor.backend.application.ports.in.DesactivarCategoriaUseCase;
import com.comedor.backend.application.ports.out.CategoryRepositoryPort;
import com.comedor.backend.infrastructure.adapters.in.web.dto.response.CategoriaResponseDTO;

public class DesactivarCategoriaService implements DesactivarCategoriaUseCase {

    private final CategoryRepositoryPort categoryRepositoryPort;
    private final CategoryMapper categoryMapper;
    public DesactivarCategoriaService(CategoryRepositoryPort repository, CategoryMapper mapper) {
        this.categoryRepositoryPort = repository;
        this.categoryMapper = mapper;
    }


    @Override
    public CategoriaResponseDTO desactivarCategoriaPorId(int id) {
        return categoryMapper.toCategoriaResponseDTO(categoryRepositoryPort.deactivateById(id));
    }
}

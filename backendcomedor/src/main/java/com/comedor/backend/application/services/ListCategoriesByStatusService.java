package com.comedor.backend.application.services;

import com.comedor.backend.application.common.mapper.CategoryMapper;
import com.comedor.backend.application.ports.in.ListCategoriesByStatusUseCase;
import com.comedor.backend.application.ports.out.CategoryRepositoryPort;
import com.comedor.backend.domain.model.enums.Status;
import com.comedor.backend.infrastructure.adapters.in.web.dto.response.CategoryResponseDTO;

import java.util.List;

public class ListCategoriesByStatusService implements ListCategoriesByStatusUseCase {
    private final CategoryRepositoryPort categoryRepositoryPort;
    private final CategoryMapper categoryMapper;

    public ListCategoriesByStatusService(CategoryRepositoryPort categoryRepositoryPort, CategoryMapper categoryMapper) {
        this.categoryRepositoryPort = categoryRepositoryPort;
        this.categoryMapper = categoryMapper;
    }

    @Override
    public List<CategoryResponseDTO> listarCategorias(Status status) {
        return categoryMapper.toListCategoriaResponseDTO(categoryRepositoryPort.getCategorys(status));
    }
}

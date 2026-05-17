package com.comedor.backend.application.services;

import com.comedor.backend.application.common.mapper.CategoryMapper;
import com.comedor.backend.application.ports.in.ListarCategoriasPorEstadoUseCase;
import com.comedor.backend.application.ports.out.CategoryRepositoryPort;
import com.comedor.backend.domain.model.enums.Estado;
import com.comedor.backend.infrastructure.adapters.in.web.dto.response.CategoriaResponseDTO;

import java.util.List;

public class ListarCategoriasPorEstadoService implements ListarCategoriasPorEstadoUseCase {
    private final CategoryRepositoryPort categoryRepositoryPort;
    private final CategoryMapper categoryMapper;

    public ListarCategoriasPorEstadoService(CategoryRepositoryPort categoryRepositoryPort, CategoryMapper categoryMapper) {
        this.categoryRepositoryPort = categoryRepositoryPort;
        this.categoryMapper = categoryMapper;
    }

    @Override
    public List<CategoriaResponseDTO> listarCategorias(Estado estado) {
        return categoryMapper.toListCategoriaResponseDTO(categoryRepositoryPort.getCategorys(estado));
    }
}

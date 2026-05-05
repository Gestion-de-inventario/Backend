package com.comedor.backend.application.services;

import com.comedor.backend.application.common.mapper.CategoriaMapper;
import com.comedor.backend.application.ports.in.DesactivarCategoriaUseCase;
import com.comedor.backend.application.ports.out.CategoriaRepositoryPort;
import com.comedor.backend.infrastructure.adapters.in.web.dto.response.CategoriaResponseDTO;

public class DesactivarCategoriaService implements DesactivarCategoriaUseCase {

    private final CategoriaRepositoryPort categoriaRepositoryPort;
    private final CategoriaMapper categoriaMapper;
    public DesactivarCategoriaService(CategoriaRepositoryPort repository, CategoriaMapper mapper) {
        this.categoriaRepositoryPort = repository;
        this.categoriaMapper = mapper;
    }


    @Override
    public CategoriaResponseDTO desactivarCategoriaPorId(int id) {
        return categoriaMapper.toCategoriaResponseDTO(categoriaRepositoryPort.deactivateById(id));
    }
}

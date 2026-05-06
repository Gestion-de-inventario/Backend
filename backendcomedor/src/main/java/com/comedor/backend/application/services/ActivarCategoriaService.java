package com.comedor.backend.application.services;

import com.comedor.backend.application.common.mapper.CategoriaMapper;
import com.comedor.backend.application.ports.in.ActivarCategoriaUseCase;
import com.comedor.backend.application.ports.out.CategoriaRepositoryPort;
import com.comedor.backend.infrastructure.adapters.in.web.dto.response.CategoriaResponseDTO;

public class ActivarCategoriaService implements ActivarCategoriaUseCase {
    private final CategoriaRepositoryPort categoriaRepositoryPort;
    private final CategoriaMapper categoriaMapper;

    public ActivarCategoriaService(CategoriaRepositoryPort categoriaRepositoryPort, CategoriaMapper categoriaMapper) {
        this.categoriaRepositoryPort = categoriaRepositoryPort;
        this.categoriaMapper = categoriaMapper;
    }


    @Override
    public CategoriaResponseDTO activarCategoriaPorId(int id) {
        return categoriaMapper.toCategoriaResponseDTO(categoriaRepositoryPort.activateById(id));
    }
}

package com.comedor.backend.application.services;

import com.comedor.backend.application.common.mapper.CategoriaMapper;
import com.comedor.backend.application.ports.in.ListarCategoriasPorEstadoUseCase;
import com.comedor.backend.application.ports.out.CategoriaRepositoryPort;
import com.comedor.backend.domain.model.enums.Estado;
import com.comedor.backend.infrastructure.adapters.in.web.dto.response.CategoriaResponseDTO;

import java.util.List;

public class ListarCategoriasPorEstadoService implements ListarCategoriasPorEstadoUseCase {
    private final CategoriaRepositoryPort categoriaRepositoryPort;
    private final CategoriaMapper categoriaMapper;

    public ListarCategoriasPorEstadoService(CategoriaRepositoryPort categoriaRepositoryPort, CategoriaMapper categoriaMapper) {
        this.categoriaRepositoryPort = categoriaRepositoryPort;
        this.categoriaMapper = categoriaMapper;
    }

    @Override
    public List<CategoriaResponseDTO> listarCategorias(Estado estado) {
        return categoriaMapper.toListCategoriaResponseDTO(categoriaRepositoryPort.getCategorys(estado));
    }
}

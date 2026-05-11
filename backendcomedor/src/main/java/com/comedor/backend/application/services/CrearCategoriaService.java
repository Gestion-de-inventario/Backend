package com.comedor.backend.application.services;

import com.comedor.backend.application.common.mapper.CategoriaMapper;
import com.comedor.backend.application.ports.in.CrearCategoriaUseCase;
import com.comedor.backend.application.ports.out.CategoriaRepositoryPort;
import com.comedor.backend.domain.exceptions.CategoriaExistenteException;
import com.comedor.backend.domain.model.Categoria;
import com.comedor.backend.infrastructure.adapters.in.web.dto.request.CategoriaRequestDTO;
import com.comedor.backend.infrastructure.adapters.in.web.dto.response.CategoriaResponseDTO;


public class CrearCategoriaService implements CrearCategoriaUseCase {
    private final CategoriaRepositoryPort categoriaRepositoryPort;
    private final CategoriaMapper categoriaMapper;
    public CrearCategoriaService(CategoriaRepositoryPort categoriaRepositoryPort, CategoriaMapper categoriaMapper) {
        this.categoriaRepositoryPort = categoriaRepositoryPort;
        this.categoriaMapper = categoriaMapper;
    }


    @Override
    public CategoriaResponseDTO crearCategoria(CategoriaRequestDTO categoriaRequestDTO) {
        if(categoriaRepositoryPort.existByName(categoriaRequestDTO.getName().toUpperCase()))
        {
            throw new CategoriaExistenteException("La categoria ya existe");
        }

        Categoria categoria = categoriaMapper.toDomain(categoriaRequestDTO);
        Categoria categoriacreada = categoriaRepositoryPort.createCategory(categoria);
        return categoriaMapper.toCategoriaResponseDTO(categoriacreada);
    }


}

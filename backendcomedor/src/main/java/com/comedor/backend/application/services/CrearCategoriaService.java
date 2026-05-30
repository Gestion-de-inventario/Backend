package com.comedor.backend.application.services;

import com.comedor.backend.application.common.mapper.CategoryMapper;
import com.comedor.backend.application.ports.in.CrearCategoriaUseCase;
import com.comedor.backend.application.ports.out.CategoryRepositoryPort;
import com.comedor.backend.domain.exceptions.CategoriaExistenteException;
import com.comedor.backend.domain.model.Category;
import com.comedor.backend.infrastructure.adapters.in.web.dto.request.CategoriaRequestDTO;
import com.comedor.backend.infrastructure.adapters.in.web.dto.response.CategoriaResponseDTO;


public class CrearCategoriaService implements CrearCategoriaUseCase {
    private final CategoryRepositoryPort categoryRepositoryPort;
    private final CategoryMapper categoryMapper;
    public CrearCategoriaService(CategoryRepositoryPort categoryRepositoryPort, CategoryMapper categoryMapper) {
        this.categoryRepositoryPort = categoryRepositoryPort;
        this.categoryMapper = categoryMapper;
    }


    @Override
    public CategoriaResponseDTO crearCategoria(CategoriaRequestDTO categoriaRequestDTO) {
        if(categoryRepositoryPort.existByName(categoriaRequestDTO.getName().toUpperCase()))
        {
            throw new CategoriaExistenteException("La categoria ya existe");
        }

        Category category = categoryMapper.toDomain(categoriaRequestDTO);
        Category categoriacreada = categoryRepositoryPort.createCategory(category);
        return categoryMapper.toCategoriaResponseDTO(categoriacreada);
    }


}

package com.comedor.backend.application.services;

import com.comedor.backend.application.common.mapper.CategoryMapper;
import com.comedor.backend.application.ports.in.CreateCategoryUseCase;
import com.comedor.backend.application.ports.out.CategoryRepositoryPort;
import com.comedor.backend.domain.exceptions.ExistingCategoryException;
import com.comedor.backend.domain.model.Category;
import com.comedor.backend.infrastructure.adapters.in.web.dto.request.CategoryRequestDTO;
import com.comedor.backend.infrastructure.adapters.in.web.dto.response.CategoryResponseDTO;


public class CreateCategoryService implements CreateCategoryUseCase {
    private final CategoryRepositoryPort categoryRepositoryPort;
    private final CategoryMapper categoryMapper;
    public CreateCategoryService(CategoryRepositoryPort categoryRepositoryPort, CategoryMapper categoryMapper) {
        this.categoryRepositoryPort = categoryRepositoryPort;
        this.categoryMapper = categoryMapper;
    }


    @Override
    public CategoryResponseDTO crearCategoria(CategoryRequestDTO categoryRequestDTO) {
        if(categoryRepositoryPort.existByName(categoryRequestDTO.getName().toUpperCase()))
        {
            throw new ExistingCategoryException("La categoria ya existe");
        }

        Category category = categoryMapper.toDomain(categoryRequestDTO);
        Category categoriacreada = categoryRepositoryPort.createCategory(category);
        return categoryMapper.toCategoriaResponseDTO(categoriacreada);
    }


}

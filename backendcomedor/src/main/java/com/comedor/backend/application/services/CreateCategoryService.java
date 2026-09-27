package com.comedor.backend.application.services;

import com.comedor.backend.application.common.mapper.CategoryMapper;
import com.comedor.backend.application.ports.in.CreateCategoryUseCase;
import com.comedor.backend.application.ports.in.RegisterAuditUseCase;
import com.comedor.backend.application.ports.out.CategoryRepositoryPort;
import com.comedor.backend.domain.exceptions.ExistingCategoryException;
import com.comedor.backend.domain.model.Category;
import com.comedor.backend.domain.model.enums.AuditAction;
import com.comedor.backend.infrastructure.adapters.in.web.dto.request.AuditRequestDTO;
import com.comedor.backend.infrastructure.adapters.in.web.dto.request.CategoryRequestDTO;
import com.comedor.backend.infrastructure.adapters.in.web.dto.response.CategoryResponseDTO;

import java.util.Map;


public class CreateCategoryService implements CreateCategoryUseCase {
    private final CategoryRepositoryPort categoryRepositoryPort;
    private final CategoryMapper categoryMapper;
    private final RegisterAuditUseCase registerAuditUseCase;
    public CreateCategoryService(CategoryRepositoryPort categoryRepositoryPort, CategoryMapper categoryMapper, RegisterAuditUseCase registerAuditUseCase) {
        this.categoryRepositoryPort = categoryRepositoryPort;
        this.categoryMapper = categoryMapper;
        this.registerAuditUseCase = registerAuditUseCase;
    }


    @Override
    public CategoryResponseDTO crearCategoria(CategoryRequestDTO categoryRequestDTO) {
        if(categoryRepositoryPort.existByName(categoryRequestDTO.getName().toUpperCase()))
        {
            throw new ExistingCategoryException("La categoria ya existe");
        }

        Category category = categoryMapper.toDomain(categoryRequestDTO);
        Category categoriacreada = categoryRepositoryPort.createCategory(category);

        registerAuditUseCase.registrar(
                new AuditRequestDTO(
                        "Categoría",
                        categoriacreada.getId(),
                        categoriacreada.getName(),
                        AuditAction.CREACION,
                        Map.of(
                                "Nombre", categoriacreada.getName()
                        )
                )
        );
        return categoryMapper.toCategoriaResponseDTO(categoriacreada);
    }


}

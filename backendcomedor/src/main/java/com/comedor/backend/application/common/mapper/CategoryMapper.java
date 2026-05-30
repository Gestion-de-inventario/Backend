package com.comedor.backend.application.common.mapper;

import com.comedor.backend.domain.model.Category;
import com.comedor.backend.infrastructure.adapters.in.web.dto.request.CategoriaRequestDTO;
import com.comedor.backend.infrastructure.adapters.in.web.dto.response.CategoriaResponseDTO;
import org.springframework.stereotype.Component;

import java.util.List;


@Component
public class CategoryMapper {

    public Category toDomain(CategoriaRequestDTO categoriaRequestDTO)
    {
        if(categoriaRequestDTO == null) return null;
        Category category = new Category();
        category.setName(categoriaRequestDTO.getName());
        return category;
    }

    public CategoriaResponseDTO toCategoriaResponseDTO(Category category)
    {
        if(category == null) return null;
        CategoriaResponseDTO categoriaResponseDTO = new CategoriaResponseDTO();

        categoriaResponseDTO.setId(category.getId());
        categoriaResponseDTO.setName(category.getName());
        categoriaResponseDTO.setStatus(category.getStatus());
        return categoriaResponseDTO;
    }

    public List<CategoriaResponseDTO> toListCategoriaResponseDTO (List<Category> categories)
    {
        if(categories == null) return null;
        return categories.stream().map(this::toCategoriaResponseDTO).toList();
    }
}

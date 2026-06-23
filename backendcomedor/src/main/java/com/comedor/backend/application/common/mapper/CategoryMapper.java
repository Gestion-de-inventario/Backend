package com.comedor.backend.application.common.mapper;

import com.comedor.backend.domain.model.Category;
import com.comedor.backend.infrastructure.adapters.in.web.dto.request.CategoryRequestDTO;
import com.comedor.backend.infrastructure.adapters.in.web.dto.response.CategoryResponseDTO;
import org.springframework.stereotype.Component;

import java.util.List;


@Component
public class CategoryMapper {

    public Category toDomain(CategoryRequestDTO categoryRequestDTO)
    {
        if(categoryRequestDTO == null) return null;
        Category category = new Category();
        category.setName(categoryRequestDTO.getName());
        return category;
    }

    public CategoryResponseDTO toCategoriaResponseDTO(Category category)
    {
        if(category == null) return null;
        CategoryResponseDTO categoryResponseDTO = new CategoryResponseDTO();

        categoryResponseDTO.setId(category.getId());
        categoryResponseDTO.setName(category.getName());
        categoryResponseDTO.setStatus(category.getStatus());
        return categoryResponseDTO;
    }

    public List<CategoryResponseDTO> toListCategoriaResponseDTO (List<Category> categories)
    {
        if(categories == null) return null;
        return categories.stream().map(this::toCategoriaResponseDTO).toList();
    }
}

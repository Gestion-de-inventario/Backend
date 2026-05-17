package com.comedor.backend.infrastructure.adapters.out.persistence.mapper;

import com.comedor.backend.domain.model.Category;
import com.comedor.backend.infrastructure.adapters.out.persistence.entity.CategoryEntity;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class CategoryEntityMapper {

    public Category toDomain(CategoryEntity categoryEntity)
    {
        if (categoryEntity == null) return null;
        Category category = new Category();
        category.setId(categoryEntity.getId());
        category.setName(categoryEntity.getName());
        category.setStatus(categoryEntity.getStatus());
        return category;
    }

    public CategoryEntity toEntity(Category category)
    {
        if (category == null) return null;
        CategoryEntity categoryEntity = new CategoryEntity();
        categoryEntity.setName(category.getName());
        categoryEntity.setStatus(category.getStatus());
        return categoryEntity;
    }
    public List<Category> toListDomain(List<CategoryEntity> entities) {
        return entities.stream().map(this::toDomain).toList();
    }
}

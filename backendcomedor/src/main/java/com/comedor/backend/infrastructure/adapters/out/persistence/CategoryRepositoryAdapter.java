package com.comedor.backend.infrastructure.adapters.out.persistence;

import com.comedor.backend.application.ports.out.CategoryRepositoryPort;
import com.comedor.backend.domain.exceptions.CategoriaNoEncontradaException;
import com.comedor.backend.domain.model.Category;
import com.comedor.backend.domain.model.enums.Estado;
import com.comedor.backend.infrastructure.adapters.out.persistence.entity.CategoryEntity;
import com.comedor.backend.infrastructure.adapters.out.persistence.mapper.CategoryEntityMapper;
import com.comedor.backend.infrastructure.adapters.out.persistence.repository.CategoryJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class CategoryRepositoryAdapter implements CategoryRepositoryPort {

    private final CategoryJpaRepository categoryJpaRepository;
    private final CategoryEntityMapper categoryEntityMapper;

    @Override
    public Category createCategory(Category category) {
        CategoryEntity entity = categoryEntityMapper.toEntity(category);
        CategoryEntity categoryEntity = categoryJpaRepository.save(entity);
        return (categoryEntityMapper.toDomain(categoryEntity));
    }

    @Override
    public Category deactivateById(int id) {

        CategoryEntity entity = categoryJpaRepository.findById(id).orElseThrow(()-> new CategoriaNoEncontradaException("Categoria no encontrada"));
        entity.setStatus(Estado.INACTIVO);
        return categoryEntityMapper.toDomain(categoryJpaRepository.save(entity));
    }

    @Override
    public Category activateById(int id) {
        CategoryEntity entity = categoryJpaRepository.findById(id).orElseThrow(()-> new CategoriaNoEncontradaException("Categoria no encontrada"));
        entity.setStatus(Estado.ACTIVO);
        return categoryEntityMapper.toDomain(categoryJpaRepository.save(entity));
    }

    @Override
    public List<Category> getCategorys(Estado estado) {
            if (estado == null) {
                return categoryEntityMapper.toListDomain(
                        categoryJpaRepository.findAll()
                );
            } else if (estado == Estado.ACTIVO) {
                return categoryEntityMapper.toListDomain(
                        categoryJpaRepository.getAllCategoriasActivas());
            }
                return categoryEntityMapper.toListDomain(
                        categoryJpaRepository.getAllCategoriasInactivas()
                );
        }

    @Override
    public boolean existByName(String name) {
        return categoryJpaRepository.existsByName(name);
    }

    @Override
    public Category getCategoryById(int id) {

        CategoryEntity categoryEntity = categoryJpaRepository.findById(id).orElseThrow(()-> new CategoriaNoEncontradaException("Categoria no encontrada"));
        return categoryEntityMapper.toDomain(categoryEntity);
    }

}

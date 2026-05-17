package com.comedor.backend.infrastructure.adapters.out.persistence.repository;

import com.comedor.backend.infrastructure.adapters.out.persistence.entity.CategoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface CategoryJpaRepository extends JpaRepository<CategoryEntity,Integer> {

    @Query("SELECT c FROM CategoryEntity c " +
            "WHERE c.status = com.comedor.backend.domain.model.enums.Estado.INACTIVO")
    List<CategoryEntity> getAllCategoriasInactivas();
    @Query("SELECT c FROM CategoryEntity c " +
            "WHERE c.status = com.comedor.backend.domain.model.enums.Estado.ACTIVO")
    List<CategoryEntity> getAllCategoriasActivas();
    Optional<CategoryEntity> findById(int id);
    boolean existsByName(String name);
}

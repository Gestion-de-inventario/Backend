package com.comedor.backend.infrastructure.adapters.out.persistence.repository;

import com.comedor.backend.domain.model.enums.Estado;
import com.comedor.backend.infrastructure.adapters.out.persistence.entity.CategoriaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface CategoriaJpaRepository extends JpaRepository<CategoriaEntity,Integer> {

    @Query("SELECT c FROM CategoriaEntity c " +
            "WHERE c.status = com.comedor.backend.domain.model.enums.Estado.INACTIVO")
    List<CategoriaEntity> getAllCategoriasInactivas();
    @Query("SELECT c FROM CategoriaEntity c " +
            "WHERE c.status = com.comedor.backend.domain.model.enums.Estado.ACTIVO")
    List<CategoriaEntity> getAllCategoriasActivas();

    boolean existsByName(String name);
}

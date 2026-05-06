package com.comedor.backend.infrastructure.adapters.out.persistence.repository;

import com.comedor.backend.infrastructure.adapters.out.persistence.entity.EtiquetaEntity;
import com.comedor.backend.infrastructure.adapters.out.persistence.entity.ProductoEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ProductoJpaRepository  extends JpaRepository<ProductoEntity, Integer> {
    @Query("SELECT p FROM ProductoEntity p " +
            "WHERE p.status = com.comedor.backend.domain.model.enums.Estado.INACTIVO")
    List<ProductoEntity> getAllProductosInactivos();
    @Query("SELECT p FROM ProductoEntity p " +
            "WHERE p.status = com.comedor.backend.domain.model.enums.Estado.ACTIVO")
    List<ProductoEntity> getAllProductosActivos();

    boolean existsByName(String name);
}

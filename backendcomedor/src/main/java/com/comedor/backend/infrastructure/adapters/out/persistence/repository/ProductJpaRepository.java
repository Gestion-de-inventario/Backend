package com.comedor.backend.infrastructure.adapters.out.persistence.repository;

import com.comedor.backend.infrastructure.adapters.out.persistence.entity.ProductEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ProductJpaRepository extends JpaRepository<ProductEntity, Integer> {
    @Query("SELECT p FROM ProductEntity p " +
            "WHERE p.status = com.comedor.backend.domain.model.enums.Estado.INACTIVO")
    List<ProductEntity> getAllProductosInactivos();
    @Query("SELECT p FROM ProductEntity p " +
            "WHERE p.status = com.comedor.backend.domain.model.enums.Estado.ACTIVO")
    List<ProductEntity> getAllProductosActivos();

    boolean existsByName(String name);

    boolean existsByNameAndIdNot(String name, int id);

    @Query("SELECT p FROM ProductEntity p " +
            "WHERE p.stock <= p.reorderPoint " +
            "AND p.status = com.comedor.backend.domain.model.enums.Estado.ACTIVO")
    List<ProductEntity> getProductosBajoStockMinimo();
}

package com.comedor.backend.infrastructure.adapters.out.persistence.repository;

import com.comedor.backend.infrastructure.adapters.out.persistence.entity.ProductEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ProductJpaRepository extends JpaRepository<ProductEntity, Integer> {
    @Query("SELECT p FROM ProductEntity p " +
            "WHERE p.status = com.comedor.backend.domain.model.enums.Status.INACTIVO")
    List<ProductEntity> getAllProductosInactivos();
    @Query("SELECT p FROM ProductEntity p " +
            "WHERE p.status = com.comedor.backend.domain.model.enums.Status.ACTIVO")
    List<ProductEntity> getAllProductosActivos();

    boolean existsByName(String name);

    boolean existsByNameAndIdNot(String name, int id);

    @Query("SELECT p FROM ProductEntity p " +
            "WHERE p.stock <= p.reorderPoint " +
            "AND p.status = com.comedor.backend.domain.model.enums.Status.ACTIVO")
    List<ProductEntity> getProductosBajoStockMinimo();

    // CA 4: Top 5 productos con MENOS stock (sin contar los que tienen 0)
    @Query("SELECT p FROM ProductEntity p " +
            "WHERE p.status = com.comedor.backend.domain.model.enums.Status.ACTIVO " +
            "AND p.stock > 0 " +
            "ORDER BY p.stock ASC " +
            "LIMIT 5")
    List<ProductEntity> findTop5LowStock();

    // CA 5: Top 5 productos con MÁS stock
    @Query("SELECT p FROM ProductEntity p " +
            "WHERE p.status = com.comedor.backend.domain.model.enums.Status.ACTIVO " +
            "ORDER BY p.stock DESC " +
            "LIMIT 5")
    List<ProductEntity> findTop5HighStock();
}

package com.comedor.backend.infrastructure.adapters.out.persistence.repository;

import com.comedor.backend.infrastructure.adapters.out.persistence.entity.InventoryLotEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface InventoryLotJpaRepository
        extends JpaRepository<InventoryLotEntity, Integer> {

    @Query("""
        SELECT il
        FROM InventoryLotEntity il
        WHERE il.product.id = :productId
          AND il.remainingQuantity > 0
        ORDER BY il.entryDate ASC
    """)
    List<InventoryLotEntity> findAvailableByProductIdOrderByFifo(
            @Param("productId") Integer productId
    );
}
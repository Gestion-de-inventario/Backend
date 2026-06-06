package com.comedor.backend.infrastructure.adapters.out.persistence.repository;

import com.comedor.backend.domain.model.PurchaseDetail;
import com.comedor.backend.infrastructure.adapters.out.persistence.entity.PurchaseDetailEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PurchaseDetailJpaRepository extends JpaRepository<PurchaseDetailEntity, Integer> {
    @Query("SELECT pd FROM PurchaseDetailEntity pd " +
            "JOIN pd.purchase p " +
            "WHERE pd.product.id = :productId " +
            "ORDER BY p.purchaseDate ASC")
    List<PurchaseDetailEntity> findAvailableByProductIdOrderByPurchaseDateAsc(@Param("productId") Integer productId);
}

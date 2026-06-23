package com.comedor.backend.infrastructure.adapters.out.persistence.repository;

import com.comedor.backend.domain.model.enums.StatusOrder;
import com.comedor.backend.infrastructure.adapters.out.persistence.entity.PurchaseEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;


import java.time.LocalDate;

public interface PurchaseJpaRepository
        extends JpaRepository<PurchaseEntity,Integer>,
        JpaSpecificationExecutor<PurchaseEntity> {

    Page<PurchaseEntity> findAll(Pageable pageable);

    Page<PurchaseEntity> findByStatus(
            StatusOrder status,
            Pageable pageable
    );

    Page<PurchaseEntity> findByPurchaseDateBetween(
            LocalDate startDate,
            LocalDate endDate,
            Pageable pageable
    );

    Page<PurchaseEntity> findByPurchaseDateBetweenAndStatus(
            LocalDate startDate,
            LocalDate endDate,
            StatusOrder status,
            Pageable pageable
    );
}
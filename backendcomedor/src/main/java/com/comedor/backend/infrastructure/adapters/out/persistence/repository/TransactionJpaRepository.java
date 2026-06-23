package com.comedor.backend.infrastructure.adapters.out.persistence.repository;


import com.comedor.backend.infrastructure.adapters.out.persistence.entity.TransactionsEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.List;

public interface TransactionJpaRepository extends JpaRepository<TransactionsEntity, Integer> ,
        JpaSpecificationExecutor<TransactionsEntity> {
    boolean existsByProductId(int productoId);

    @Query(value = "SELECT * FROM transactions t WHERE " +
            "(?1::date IS NULL OR t.date_time::date >= ?1::date) AND " +
            "(?2::date IS NULL OR t.date_time::date <= ?2::date) " +
            "ORDER BY t.id DESC",
            countQuery = "SELECT COUNT(*) FROM transactions t WHERE " +
                    "(?1::date IS NULL OR t.date_time::date >= ?1::date) AND " +
                    "(?2::date IS NULL OR t.date_time::date <= ?2::date)",
            nativeQuery = true)
    Page<TransactionsEntity> findByPeriod(String fechaInicio, String fechaFin, Pageable pageable);

    @Query(value = "SELECT * FROM transactions t WHERE " +
            "(?1::date IS NULL OR t.date_time::date >= ?1::date) AND " +
            "(?2::date IS NULL OR t.date_time::date <= ?2::date) " +
            "ORDER BY t.id DESC",
            nativeQuery = true)
    List<TransactionsEntity> findByPeriodList(String fechaInicio, String fechaFin);
}

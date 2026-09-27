package com.comedor.backend.infrastructure.adapters.out.persistence.repository;


import com.comedor.backend.infrastructure.adapters.out.persistence.entity.AuditEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface AuditJpaRepository
        extends JpaRepository<AuditEntity, Integer>,
        JpaSpecificationExecutor<AuditEntity> {


    @Query(value = """
            SELECT *
            FROM audit_table a
            WHERE (:fechaInicio IS NULL OR a.date_time::date >= CAST(:fechaInicio AS date))
              AND (:fechaFin IS NULL OR a.date_time::date <= CAST(:fechaFin AS date))
            ORDER BY a.id DESC
            """,
            nativeQuery = true)
    List<AuditEntity> findByPeriodList(
            @Param("fechaInicio") String fechaInicio,
            @Param("fechaFin") String fechaFin
    );
}

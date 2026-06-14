package com.comedor.backend.infrastructure.adapters.out.persistence.repository;

import com.comedor.backend.infrastructure.adapters.out.persistence.entity.ModificationsEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface ModificationsJpaRepository extends JpaRepository<ModificationsEntity, Integer> {
    @Query(value = "SELECT * FROM modifications m WHERE " +
            "(?1::date IS NULL OR m.date_time::date >= ?1::date) AND " +
            "(?2::date IS NULL OR m.date_time::date <= ?2::date) " +
            "ORDER BY m.id DESC",
            countQuery = "SELECT COUNT(*) FROM modifications m WHERE " +
                    "(?1::date IS NULL OR m.date_time::date >= ?1::date) AND " +
                    "(?2::date IS NULL OR m.date_time::date <= ?2::date)",
            nativeQuery = true)
    Page<ModificationsEntity> findByPeriod(String fechaInicio, String fechaFin, Pageable pageable);

    @Query(value = "SELECT * FROM modifications m WHERE " +
            "(?1::date IS NULL OR m.date_time::date >= ?1::date) AND " +
            "(?2::date IS NULL OR m.date_time::date <= ?2::date) " +
            "ORDER BY m.id DESC",
            nativeQuery = true)
    List<ModificationsEntity> findByPeriodList(String fechaInicio, String fechaFin);
}

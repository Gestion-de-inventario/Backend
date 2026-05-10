package com.comedor.backend.infrastructure.adapters.out.persistence.repository;

import com.comedor.backend.infrastructure.adapters.out.persistence.entity.ReporteMenuEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.Date;

public interface ReporteMenuJpaRepository extends JpaRepository<ReporteMenuEntity, Integer> {

    boolean existsByDate(LocalDate date);
    ReporteMenuEntity findByDate(LocalDate date);

}

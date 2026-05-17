package com.comedor.backend.infrastructure.adapters.out.persistence.repository;

import com.comedor.backend.infrastructure.adapters.out.persistence.entity.MenuReportEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;

public interface MenuReportJpaRepository extends JpaRepository<MenuReportEntity, Integer> {

    boolean existsByDate(LocalDate date);
    MenuReportEntity findByDate(LocalDate date);

}

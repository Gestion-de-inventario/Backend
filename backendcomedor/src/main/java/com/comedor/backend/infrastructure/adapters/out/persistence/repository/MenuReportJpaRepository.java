package com.comedor.backend.infrastructure.adapters.out.persistence.repository;

import com.comedor.backend.infrastructure.adapters.out.persistence.entity.MenuReportEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.time.LocalDate;

public interface MenuReportJpaRepository extends
        JpaRepository<MenuReportEntity, Integer>,
        JpaSpecificationExecutor<MenuReportEntity> {

    boolean existsByDate(LocalDate date);
    MenuReportEntity findByDate(LocalDate date);
    Page<MenuReportEntity> findAll(Pageable pageable);

}

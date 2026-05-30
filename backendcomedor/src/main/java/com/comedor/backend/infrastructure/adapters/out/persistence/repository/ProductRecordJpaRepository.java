package com.comedor.backend.infrastructure.adapters.out.persistence.repository;

import com.comedor.backend.infrastructure.adapters.out.persistence.entity.RecordEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductRecordJpaRepository extends JpaRepository<RecordEntity,Integer> {
    List<RecordEntity> findByReportId(int reporteId);

}

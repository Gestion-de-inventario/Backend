package com.comedor.backend.infrastructure.adapters.out.persistence.repository;

import com.comedor.backend.infrastructure.adapters.out.persistence.entity.BeneficiaryControlEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BeneficiaryControlJpaRepository extends JpaRepository<BeneficiaryControlEntity, Integer> {
    List<BeneficiaryControlEntity> findByReportId(int reporteId);
}

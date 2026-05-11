package com.comedor.backend.infrastructure.adapters.out.persistence.repository;

import com.comedor.backend.infrastructure.adapters.out.persistence.entity.ControlBeneficiarioEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ControlBeneficiarioJpaRepository extends JpaRepository<ControlBeneficiarioEntity, Integer> {
    List<ControlBeneficiarioEntity> findByReporteId(int reporteId);
}

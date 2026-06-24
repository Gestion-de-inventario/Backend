package com.comedor.backend.infrastructure.adapters.out.persistence.repository;

import com.comedor.backend.infrastructure.adapters.out.persistence.entity.EmpresaConfigEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EmpresaConfigJpaRepository extends JpaRepository<EmpresaConfigEntity, Integer> {
}

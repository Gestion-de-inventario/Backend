package com.comedor.backend.infrastructure.adapters.out.persistence.repository;

import com.comedor.backend.infrastructure.adapters.out.persistence.entity.BeneficiarioEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BeneficiarioJpaRepository extends JpaRepository<BeneficiarioEntity, Integer> {
    boolean existsByDni(String dni);

    Optional<BeneficiarioEntity> findByDni(String dni);
}

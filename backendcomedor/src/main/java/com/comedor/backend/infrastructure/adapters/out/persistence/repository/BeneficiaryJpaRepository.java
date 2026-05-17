package com.comedor.backend.infrastructure.adapters.out.persistence.repository;

import com.comedor.backend.infrastructure.adapters.out.persistence.entity.BeneficiaryEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface BeneficiaryJpaRepository extends JpaRepository<BeneficiaryEntity, Integer> {
    boolean existsByDni(String dni);

    Optional<BeneficiaryEntity> findByDni(String dni);

    @Query("SELECT b FROM BeneficiaryEntity b " +
            "WHERE b.status = com.comedor.backend.domain.model.enums.Estado.INACTIVO")
    List<BeneficiaryEntity> getAllBeneficiariosInactivos();
    @Query("SELECT b FROM BeneficiaryEntity b " +
            "WHERE b.status = com.comedor.backend.domain.model.enums.Estado.ACTIVO")
    List<BeneficiaryEntity> getAllBeneficiariosActivos();

}

package com.comedor.backend.infrastructure.adapters.out.persistence.repository;

import com.comedor.backend.infrastructure.adapters.out.persistence.entity.BeneficiarioEntity;
import com.comedor.backend.infrastructure.adapters.out.persistence.entity.ProductoEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface BeneficiarioJpaRepository extends JpaRepository<BeneficiarioEntity, Integer> {
    boolean existsByDni(String dni);

    Optional<BeneficiarioEntity> findByDni(String dni);

    @Query("SELECT b FROM BeneficiarioEntity b " +
            "WHERE b.status = com.comedor.backend.domain.model.enums.Estado.INACTIVO")
    List<BeneficiarioEntity> getAllBeneficiariosInactivos();
    @Query("SELECT b FROM BeneficiarioEntity b " +
            "WHERE b.status = com.comedor.backend.domain.model.enums.Estado.ACTIVO")
    List<BeneficiarioEntity> getAllBeneficiariosActivos();

}

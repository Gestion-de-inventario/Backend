package com.comedor.backend.infrastructure.adapters.out.persistence.repository;

import com.comedor.backend.domain.model.enums.Estado;
import com.comedor.backend.infrastructure.adapters.out.persistence.entity.BeneficiaryTypeEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BeneficiaryTypeJpaRepository extends JpaRepository<BeneficiaryTypeEntity, Integer> {
    List<BeneficiaryTypeEntity> findByStatus(Estado status);

    boolean existsByName(String name);

    boolean existsByNameAndIdNot(String name, Integer id);


}

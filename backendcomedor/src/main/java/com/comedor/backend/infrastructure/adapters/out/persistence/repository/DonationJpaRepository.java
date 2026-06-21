package com.comedor.backend.infrastructure.adapters.out.persistence.repository;

import com.comedor.backend.infrastructure.adapters.out.persistence.entity.DonationEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface DonationJpaRepository extends JpaRepository<DonationEntity,Integer>,
        JpaSpecificationExecutor<DonationEntity> {
}

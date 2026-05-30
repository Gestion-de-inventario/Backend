package com.comedor.backend.infrastructure.adapters.out.persistence.repository;

import com.comedor.backend.infrastructure.adapters.out.persistence.entity.ModificationsEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ModificationsJpaRepository extends JpaRepository<ModificationsEntity, Integer> {

}

package com.comedor.backend.infrastructure.adapters.out.persistence.repository;

import com.comedor.backend.domain.model.enums.PermissionCode;
import com.comedor.backend.infrastructure.adapters.out.persistence.entity.PermissionEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.Set;

public interface PermissionJpaRepository extends JpaRepository<PermissionEntity, Integer> {

    Set<PermissionEntity> findByCodeIn(Set<PermissionCode> codes);

    Optional<PermissionEntity> findByCode(PermissionCode code);
}
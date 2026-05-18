package com.comedor.backend.infrastructure.adapters.out.persistence.repository;
import com.comedor.backend.domain.model.enums.Estado;
import com.comedor.backend.infrastructure.adapters.out.persistence.entity.RoleEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface RoleJpaRepository extends JpaRepository<RoleEntity, Integer> {
    Optional<RoleEntity> findByName(String name);

    List<RoleEntity> findByStatus(Estado status);

    boolean existsByName(String name);

}

package com.comedor.backend.infrastructure.adapters.out.persistence.repository;
import com.comedor.backend.infrastructure.adapters.out.persistence.entity.RoleEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleJpaRepository extends JpaRepository<RoleEntity, Integer> {

}

package com.comedor.backend.infrastructure.adapters.out.persistence.repository;
import com.comedor.backend.infrastructure.adapters.out.persistence.entity.RolEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RolJpaRepository extends JpaRepository<RolEntity, Integer> {

}

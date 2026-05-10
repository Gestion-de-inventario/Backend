package com.comedor.backend.infrastructure.adapters.out.persistence.repository;

import com.comedor.backend.infrastructure.adapters.out.persistence.entity.TransaccionesEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TransaccionJpaRepository extends JpaRepository<TransaccionesEntity, Integer> {

}

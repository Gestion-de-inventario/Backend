package com.comedor.backend.infrastructure.adapters.out.persistence.repository;

import com.comedor.backend.infrastructure.adapters.out.persistence.entity.RegistroEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RegistroProductoJpaRepository extends JpaRepository<RegistroEntity,Integer> {

}

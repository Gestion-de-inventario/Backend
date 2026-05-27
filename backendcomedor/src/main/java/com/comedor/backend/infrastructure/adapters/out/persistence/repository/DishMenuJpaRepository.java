package com.comedor.backend.infrastructure.adapters.out.persistence.repository;

import com.comedor.backend.infrastructure.adapters.out.persistence.entity.DishMenuEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DishMenuJpaRepository
        extends JpaRepository<DishMenuEntity, Integer> {
}
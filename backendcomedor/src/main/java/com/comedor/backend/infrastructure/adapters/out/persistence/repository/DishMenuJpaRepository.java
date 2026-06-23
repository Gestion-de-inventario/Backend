package com.comedor.backend.infrastructure.adapters.out.persistence.repository;

import com.comedor.backend.domain.model.enums.Status;
import com.comedor.backend.infrastructure.adapters.out.persistence.entity.DishMenuEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface DishMenuJpaRepository extends JpaRepository<DishMenuEntity, Integer> {
    @Query("SELECT dm FROM DishMenuEntity dm " +
            "JOIN FETCH dm.supplies s " +
            "JOIN FETCH s.product p " +
            "WHERE dm.id = :id")
    Optional<DishMenuEntity> findByIdWithSupplies(@Param("id") Integer id);

    List<DishMenuEntity> findByStatus(Status status);
    boolean existsByNameIgnoreCase(String name);
    boolean existsByNameIgnoreCaseAndIdNot(String name, Integer id);
}
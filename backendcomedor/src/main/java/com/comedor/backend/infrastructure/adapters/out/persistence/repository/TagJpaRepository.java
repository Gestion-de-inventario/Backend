package com.comedor.backend.infrastructure.adapters.out.persistence.repository;

import com.comedor.backend.infrastructure.adapters.out.persistence.entity.TagEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface TagJpaRepository extends JpaRepository<TagEntity, Integer> {
    @Query("SELECT sc FROM TagEntity sc " +
            "WHERE sc.status = com.comedor.backend.domain.model.enums.Estado.INACTIVO")
    List<TagEntity> getAllEtiquetasInactivas();
    @Query("SELECT sc FROM TagEntity sc " +
            "WHERE sc.status = com.comedor.backend.domain.model.enums.Estado.ACTIVO")
    List<TagEntity> getAllEtiquetasActivas();
    Optional<TagEntity> findById(int id);
    boolean existsByName(String name);

}

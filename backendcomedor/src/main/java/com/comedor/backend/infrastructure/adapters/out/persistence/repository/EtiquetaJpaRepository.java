package com.comedor.backend.infrastructure.adapters.out.persistence.repository;

import com.comedor.backend.infrastructure.adapters.out.persistence.entity.EtiquetaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface EtiquetaJpaRepository extends JpaRepository<EtiquetaEntity, Integer> {
    @Query("SELECT sc FROM EtiquetaEntity sc " +
            "WHERE sc.status = com.comedor.backend.domain.model.enums.Estado.INACTIVO")
    List<EtiquetaEntity> getAllEtiquetasInactivas();
    @Query("SELECT sc FROM EtiquetaEntity sc " +
            "WHERE sc.status = com.comedor.backend.domain.model.enums.Estado.ACTIVO")
    List<EtiquetaEntity> getAllEtiquetasActivas();

    boolean existsByName(String name);

}

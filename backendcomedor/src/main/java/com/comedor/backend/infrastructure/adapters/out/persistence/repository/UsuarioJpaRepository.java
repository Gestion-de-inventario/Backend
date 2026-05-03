package com.comedor.backend.infrastructure.adapters.out.persistence.repository;
import com.comedor.backend.infrastructure.adapters.out.persistence.entity.UsuarioEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface UsuarioJpaRepository extends JpaRepository<UsuarioEntity, Integer> {

    Optional<UsuarioEntity> findByUsername(String username);
    @Query("SELECT u FROM UsuarioEntity u " +
            "JOIN FETCH u.persona " +
            "JOIN FETCH u.role " +
            "WHERE u.status = com.comedor.backend.domain.model.enums.Estado.ACTIVO")
    List<UsuarioEntity> getActiveUsers();

    @Query("SELECT u FROM UsuarioEntity u JOIN FETCH u.persona JOIN FETCH u.role")
    List<UsuarioEntity> getAllUsers();

}
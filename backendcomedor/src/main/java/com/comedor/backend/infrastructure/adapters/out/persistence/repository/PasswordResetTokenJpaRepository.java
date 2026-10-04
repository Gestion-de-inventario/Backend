package com.comedor.backend.infrastructure.adapters.out.persistence.repository;

import com.comedor.backend.infrastructure.adapters.out.persistence.entity.PasswordResetTokenEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

public interface PasswordResetTokenJpaRepository extends JpaRepository<PasswordResetTokenEntity, Integer> {
    Optional<PasswordResetTokenEntity> findByToken(String token);

    @Modifying
    @Transactional
    @Query("UPDATE PasswordResetTokenEntity p SET p.used = true WHERE p.userId = :userId AND p.used = false")
    void markAllAsUsedByUserId(@Param("userId") Integer userId);
}

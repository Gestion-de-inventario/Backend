package com.comedor.backend.infrastructure.adapters.out.persistence.repository;
import com.comedor.backend.domain.model.enums.Status;
import com.comedor.backend.infrastructure.adapters.out.persistence.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UserJpaRepository extends JpaRepository<UserEntity, Integer> {

    Optional<UserEntity> findByUsername(String username);
    @Query("SELECT u FROM UserEntity u " +
            "JOIN FETCH u.persona " +
            "JOIN FETCH u.role " +
            "WHERE u.status = com.comedor.backend.domain.model.enums.Status.ACTIVO")
    List<UserEntity> getActiveUsers();

    @Query("SELECT u FROM UserEntity u JOIN FETCH u.persona JOIN FETCH u.role")
    List<UserEntity> getAllUsers();

    boolean existsByPhone(String phone);

    boolean existsByPhoneAndIdNot(String phone, Integer id);

    boolean existsByRoleIdAndStatus(Integer roleId, Status status);

    @Query("SELECT u.phone FROM UserEntity u WHERE u.username = :username")
    Optional<String> getPhoneByUsername(@Param("username") String username);

}
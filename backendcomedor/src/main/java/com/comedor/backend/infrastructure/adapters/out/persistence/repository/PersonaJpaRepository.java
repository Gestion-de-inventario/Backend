package com.comedor.backend.infrastructure.adapters.out.persistence.repository;

import com.comedor.backend.infrastructure.adapters.out.persistence.entity.PersonaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;


public interface PersonaJpaRepository extends JpaRepository<PersonaEntity, Integer> {
    @Query("SELECT p FROM PersonaEntity p")
    List<PersonaEntity> getAllPersonas();
}

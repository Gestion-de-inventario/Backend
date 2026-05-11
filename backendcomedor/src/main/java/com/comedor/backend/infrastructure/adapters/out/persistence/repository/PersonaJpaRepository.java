package com.comedor.backend.infrastructure.adapters.out.persistence.repository;

import com.comedor.backend.infrastructure.adapters.in.web.dto.response.UsuarioBasicoDTO;
import com.comedor.backend.infrastructure.adapters.out.persistence.entity.PersonaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;


public interface PersonaJpaRepository extends JpaRepository<PersonaEntity, Integer> {
    @Query("SELECT p FROM PersonaEntity p")
    List<PersonaEntity> getAllPersonas();

    boolean existsByNameAndLastName(String name, String lastName);

    boolean existsByDni(String dni);

    boolean existsByDniAndIdNot(String dni, int id);

    boolean existsByNameAndLastNameAndIdNot(String name, String last_name,int user_id);

    @Query("SELECT new com.comedor.backend.infrastructure.adapters.in.web.dto.response.UsuarioBasicoDTO(p.id, p.name, p.user.username) " +
            "FROM PersonaEntity p WHERE p.user.id = :id")
    Optional<UsuarioBasicoDTO> findUsuarioBasicoDtoById(@Param("id") Integer id);

    List<PersonaEntity> findAllById(Iterable<Integer> ids);

}

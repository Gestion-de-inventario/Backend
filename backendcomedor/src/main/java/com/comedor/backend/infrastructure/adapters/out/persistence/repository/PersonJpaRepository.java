package com.comedor.backend.infrastructure.adapters.out.persistence.repository;

import com.comedor.backend.infrastructure.adapters.in.web.dto.response.UsuarioBasicoDTO;
import com.comedor.backend.infrastructure.adapters.out.persistence.entity.PersonEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;


public interface PersonJpaRepository extends JpaRepository<PersonEntity, Integer> {
    @Query("SELECT p FROM PersonEntity p")
    List<PersonEntity> getAllPersonas();

    boolean existsByNameAndLastName(String name, String lastName);

    boolean existsByDni(String dni);

    boolean existsByDniAndIdNot(String dni, int id);

    boolean existsByNameAndLastNameAndIdNot(String name, String last_name,int user_id);

    @Query("SELECT new com.comedor.backend.infrastructure.adapters.in.web.dto.response.UsuarioBasicoDTO(p.id, p.name, p.user.username) " +
            "FROM PersonEntity p WHERE p.user.id = :id")
    Optional<UsuarioBasicoDTO> findUsuarioBasicoDtoById(@Param("id") Integer id);

    List<PersonEntity> findAllById(Iterable<Integer> ids);

}

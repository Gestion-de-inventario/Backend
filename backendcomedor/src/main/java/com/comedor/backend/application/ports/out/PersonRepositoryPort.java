package com.comedor.backend.application.ports.out;

import com.comedor.backend.domain.model.Person;
import com.comedor.backend.infrastructure.adapters.in.web.dto.response.BasicUserResponseDTO;

import java.util.List;
import java.util.Optional;

public interface PersonRepositoryPort {
    List<Person> getAllPersonas();
    boolean existsByNameAndLastName(String name, String lastName);
    boolean existsByDni(String dni);
    boolean existsByDniAndIdNot(String dni,int id);
    Optional<BasicUserResponseDTO> findUsuarioBasicoDtoById(Integer id);
    boolean existsByNameAndLastNameAndIdNot(String name, String lastName, Integer user_id);
    List<Person> findAllByIds(List<Integer> ids);
}

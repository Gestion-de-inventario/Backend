package com.comedor.backend.application.ports.out;

import com.comedor.backend.domain.model.Persona;
import com.comedor.backend.infrastructure.adapters.in.web.dto.response.UsuarioBasicoDTO;

import java.util.List;
import java.util.Optional;

public interface PersonaRepositoryPort {
    List<Persona> getAllPersonas();
    boolean existsByNameAndLastName(String name, String lastName);
    boolean existsByDni(String dni);
    boolean existsByDniAndIdNot(String dni,int id);
    Optional<UsuarioBasicoDTO> findUsuarioBasicoDtoById(Integer id);
    boolean existsByNameAndLastNameAndIdNot(String name, String lastName, Integer user_id);
    List<Persona> findAllByIds(List<Integer> ids);
}

package com.comedor.backend.infrastructure.adapters.out.persistence;

import com.comedor.backend.application.common.mapper.PersonaMapper;
import com.comedor.backend.application.ports.out.PersonaRepositoryPort;
import com.comedor.backend.domain.model.Persona;
import com.comedor.backend.infrastructure.adapters.in.web.dto.response.UsuarioBasicoDTO;
import com.comedor.backend.infrastructure.adapters.out.persistence.mapper.PersonaEntityMapper;
import com.comedor.backend.infrastructure.adapters.out.persistence.repository.PersonaJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class PersonaRepositoryAdapter implements PersonaRepositoryPort {

    private final PersonaJpaRepository personaJpaRepository;
    private final PersonaEntityMapper personaEntityMapper;
    @Override
    public List<Persona> getAllPersonas() {
      return  personaEntityMapper.toListDomain(personaJpaRepository.getAllPersonas());
    }

    @Override
    public boolean existsByNameAndLastName(String name, String lastName) {
        return personaJpaRepository.existsByNameAndLastName(name, lastName);
    }

    @Override
    public boolean existsByDni(String dni) {
        return personaJpaRepository.existsByDni(dni);
    }

    @Override
    public boolean existsByDniAndIdNot(String dni,int id) {
        return personaJpaRepository.existsByDniAndIdNot(dni,id);
    }

    @Override
    public Optional<UsuarioBasicoDTO> findUsuarioBasicoDtoById(Integer id) {
        return Optional.empty();
    }

    @Override
    public boolean existsByNameAndLastNameAndIdNot(String name, String lastName, Integer user_id) {
        return personaJpaRepository.existsByNameAndLastNameAndIdNot(name, lastName, user_id);
    }
}

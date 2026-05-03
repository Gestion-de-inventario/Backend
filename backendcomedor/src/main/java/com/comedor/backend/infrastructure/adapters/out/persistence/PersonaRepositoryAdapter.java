package com.comedor.backend.infrastructure.adapters.out.persistence;

import com.comedor.backend.application.common.mapper.PersonaMapper;
import com.comedor.backend.application.ports.out.PersonaRepositoryPort;
import com.comedor.backend.domain.model.Persona;
import com.comedor.backend.infrastructure.adapters.out.persistence.mapper.PersonaEntityMapper;
import com.comedor.backend.infrastructure.adapters.out.persistence.repository.PersonaJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
@Component
@RequiredArgsConstructor
public class PersonaRepositoryAdapter implements PersonaRepositoryPort {

    private final PersonaJpaRepository personaJpaRepository;
    private final PersonaEntityMapper personaEntityMapper;
    @Override
    public List<Persona> getAllPersonas() {
      return  personaEntityMapper.toListDomain(personaJpaRepository.getAllPersonas());
    }
}

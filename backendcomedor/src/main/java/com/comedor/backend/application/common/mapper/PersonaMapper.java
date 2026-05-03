package com.comedor.backend.application.common.mapper;

import com.comedor.backend.domain.model.Persona;
import com.comedor.backend.infrastructure.adapters.in.web.dto.response.PersonaResponseDTO;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class PersonaMapper {

    public PersonaResponseDTO toResponseDTO(Persona persona) {
        PersonaResponseDTO personaResponseDTO = new PersonaResponseDTO();
        personaResponseDTO.setId(persona.getId());
        personaResponseDTO.setDni(persona.getDni());
        personaResponseDTO.setName(persona.getName());
        personaResponseDTO.setLastname(persona.getLastname());
        return personaResponseDTO;
    }

    public List<PersonaResponseDTO> toListPersonaResponseDTO(List<Persona> personas) {
        return personas.stream()
                .map(this::toResponseDTO).toList();
    }
}
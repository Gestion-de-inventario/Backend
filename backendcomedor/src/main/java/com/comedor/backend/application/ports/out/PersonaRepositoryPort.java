package com.comedor.backend.application.ports.out;

import com.comedor.backend.domain.model.Persona;
import java.util.List;

public interface PersonaRepositoryPort {
    List<Persona> getAllPersonas();
}

package com.comedor.backend.application.ports.in;

import com.comedor.backend.infrastructure.adapters.in.web.dto.response.PersonaResponseDTO;

import java.util.List;

public interface ListarPersonasUseCase {
 List<PersonaResponseDTO> listarPersonas();
}

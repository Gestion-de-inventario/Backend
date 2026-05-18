package com.comedor.backend.application.ports.in;

import com.comedor.backend.infrastructure.adapters.in.web.dto.response.ModificationsResponseDTO;

import java.util.List;

public interface ListarModificacionesUseCase {
    List<ModificationsResponseDTO> listar();
}

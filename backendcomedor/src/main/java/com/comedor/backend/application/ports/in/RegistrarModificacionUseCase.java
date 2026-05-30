package com.comedor.backend.application.ports.in;

import com.comedor.backend.infrastructure.adapters.in.web.dto.request.ModificationsRequestDTO;

public interface RegistrarModificacionUseCase {
    void registrar(ModificationsRequestDTO modificationsRequestDTO);
}

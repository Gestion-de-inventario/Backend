package com.comedor.backend.application.ports.in;

import com.comedor.backend.infrastructure.adapters.in.web.dto.request.ModificationsRequestDTO;

public interface RegisterModificationUseCase {
    void registrar(ModificationsRequestDTO modificationsRequestDTO);
}

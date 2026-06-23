package com.comedor.backend.application.ports.in;

import com.comedor.backend.infrastructure.adapters.in.web.dto.response.TagResponseDTO;

public interface DeactivateTagUseCase {
    TagResponseDTO desactivarEtiquetaPorId(int id);
}

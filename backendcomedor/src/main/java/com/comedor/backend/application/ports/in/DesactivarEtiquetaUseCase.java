package com.comedor.backend.application.ports.in;

import com.comedor.backend.infrastructure.adapters.in.web.dto.response.EtiquetaResponseDTO;

public interface DesactivarEtiquetaUseCase {
    EtiquetaResponseDTO desactivarEtiquetaPorId(int id);
}

package com.comedor.backend.application.ports.in;

import com.comedor.backend.infrastructure.adapters.in.web.dto.request.EtiquetaRequestDTO;
import com.comedor.backend.infrastructure.adapters.in.web.dto.response.EtiquetaResponseDTO;

public interface CrearEtiquetaUseCase {

    EtiquetaResponseDTO crearEtiqueta(EtiquetaRequestDTO etiquetaRequestDTO);
}

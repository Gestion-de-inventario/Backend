package com.comedor.backend.application.ports.in;

import com.comedor.backend.domain.model.enums.Estado;
import com.comedor.backend.infrastructure.adapters.in.web.dto.response.CategoriaResponseDTO;
import com.comedor.backend.infrastructure.adapters.in.web.dto.response.EtiquetaResponseDTO;

import java.util.List;

public interface ListarEtiquetasPorEstadoUseCase {
    List<EtiquetaResponseDTO> listarEtiquetas(Estado estado);
}

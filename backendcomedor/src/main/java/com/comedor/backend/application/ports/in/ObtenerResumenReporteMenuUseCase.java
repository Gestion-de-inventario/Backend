package com.comedor.backend.application.ports.in;

import com.comedor.backend.infrastructure.adapters.in.web.dto.response.ResumenReporteMenuResponseDTO;

public interface ObtenerResumenReporteMenuUseCase {
    ResumenReporteMenuResponseDTO obtenerResumen(int id);
}

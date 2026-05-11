package com.comedor.backend.application.ports.in;

import com.comedor.backend.infrastructure.adapters.in.web.dto.request.ReporteMenuRequestDTO;
import com.comedor.backend.infrastructure.adapters.in.web.dto.response.ReporteMenuResponseDTO;

public interface CrearReporteMenuUseCase {
    ReporteMenuResponseDTO crearReporteMenu(ReporteMenuRequestDTO reporteMenuRequestDTO);
}

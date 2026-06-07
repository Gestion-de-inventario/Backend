package com.comedor.backend.application.ports.in;

import com.comedor.backend.infrastructure.adapters.in.web.dto.request.DashboardResponseDTO;

public interface ObtenerDashboardUseCase {
    DashboardResponseDTO ejecutar(int anio, int mes);
}

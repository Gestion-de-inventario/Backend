package com.comedor.backend.application.ports.in;

import com.comedor.backend.infrastructure.adapters.in.web.dto.response.DashboardResponseDTO;

public interface GetDashboardUseCase {
    DashboardResponseDTO ejecutar(int anio, int mes);
}

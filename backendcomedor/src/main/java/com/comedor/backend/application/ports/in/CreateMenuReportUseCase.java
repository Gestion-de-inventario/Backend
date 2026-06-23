package com.comedor.backend.application.ports.in;

import com.comedor.backend.infrastructure.adapters.in.web.dto.request.MenuReportRequestDTO;
import com.comedor.backend.infrastructure.adapters.in.web.dto.response.MenuReportResponseDTO;

public interface CreateMenuReportUseCase {
    MenuReportResponseDTO crearReporteMenu(MenuReportRequestDTO menuReportRequestDTO);
}

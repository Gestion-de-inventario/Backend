package com.comedor.backend.application.ports.in;

import com.comedor.backend.infrastructure.adapters.in.web.dto.response.ReporteMenuResponseDTO;
import com.comedor.backend.infrastructure.adapters.out.persistence.entity.MenuReportEntity;

public interface GetMenuReportByIdUseCase {
    ReporteMenuResponseDTO getMenuReportById(Integer id);
}

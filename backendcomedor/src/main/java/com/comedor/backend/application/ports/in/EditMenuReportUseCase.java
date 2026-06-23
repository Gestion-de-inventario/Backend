package com.comedor.backend.application.ports.in;

import com.comedor.backend.infrastructure.adapters.in.web.dto.request.EditMenuReportRequestDTO;
import com.comedor.backend.infrastructure.adapters.in.web.dto.response.MenuReportResponseDTO;

public interface EditMenuReportUseCase {
    MenuReportResponseDTO editMenuReport(Integer id , EditMenuReportRequestDTO requestDTO);
}

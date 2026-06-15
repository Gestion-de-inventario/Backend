package com.comedor.backend.application.ports.in;

import com.comedor.backend.infrastructure.adapters.in.web.dto.response.ReporteMenuResponseDTO;

import java.time.LocalDate;

import org.springframework.data.domain.Page;

public interface ListMenuReportUseCase {
    Page<ReporteMenuResponseDTO> list(int page,
                                      int size,
                                      LocalDate startDate,
                                      LocalDate endDate);
}

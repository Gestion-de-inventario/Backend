package com.comedor.backend.application.ports.in;

import com.comedor.backend.infrastructure.adapters.in.web.dto.response.MenuReportResponseDTO;

import java.time.LocalDate;

import org.springframework.data.domain.Page;

public interface ListMenuReportUseCase {
    Page<MenuReportResponseDTO> list(int page,
                                     int size,
                                     LocalDate startDate,
                                     LocalDate endDate);
}

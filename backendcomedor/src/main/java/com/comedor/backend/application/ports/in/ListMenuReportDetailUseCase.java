package com.comedor.backend.application.ports.in;

import com.comedor.backend.infrastructure.adapters.in.web.dto.response.ListMenuReportDetailResponseDTO;
import org.springframework.data.domain.Page;

import java.time.LocalDate;

public interface ListMenuReportDetailUseCase {
    ListMenuReportDetailResponseDTO list(LocalDate startDate,
                                         LocalDate endDate);
}

package com.comedor.backend.application.ports.in;

import com.comedor.backend.domain.model.enums.AuditAction;
import com.comedor.backend.infrastructure.adapters.in.web.dto.response.AuditResponseDTO;
import org.springframework.data.domain.Page;

import java.time.LocalDate;

public interface ListAuditUseCase {
    Page<AuditResponseDTO> list(int page, int size, LocalDate fechaInicio, LocalDate fechaFin, AuditAction action);
}

package com.comedor.backend.application.services;

import com.comedor.backend.application.common.mapper.AuditMapper;
import com.comedor.backend.application.ports.in.ListAuditUseCase;
import com.comedor.backend.application.ports.out.AuditRepositoryPort;
import com.comedor.backend.domain.model.enums.AuditAction;
import com.comedor.backend.infrastructure.adapters.in.web.dto.response.AuditResponseDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.time.LocalDate;

public class ListAuditService  implements ListAuditUseCase {

    private final AuditRepositoryPort auditRepositoryPort;
    private final AuditMapper auditMapper;

    public ListAuditService(
            AuditRepositoryPort auditRepositoryPort,
            AuditMapper auditMapper) {

        this.auditRepositoryPort = auditRepositoryPort;
        this.auditMapper = auditMapper;
    }

    @Override
    public Page<AuditResponseDTO> list(int page, int size, LocalDate fechaInicio, LocalDate fechaFin, AuditAction action) {
        Pageable pageable = PageRequest.of(
                page,
                size,
                Sort.by(
                        Sort.Order.desc("dateTime"),
                        Sort.Order.desc("id")
                )
        );

        return auditRepositoryPort.showAudits(fechaInicio,fechaFin,action,pageable).map(auditMapper::toResponseDTO);
    }
}

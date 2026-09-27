package com.comedor.backend.application.ports.in;

import com.comedor.backend.infrastructure.adapters.in.web.dto.request.AuditRequestDTO;

public interface RegisterAuditUseCase  {
    void registrar(AuditRequestDTO auditRequestDTO);
}

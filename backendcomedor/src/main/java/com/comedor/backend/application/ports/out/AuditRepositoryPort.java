package com.comedor.backend.application.ports.out;

import com.comedor.backend.domain.model.Audit;
import com.comedor.backend.domain.model.enums.AuditAction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;

public interface AuditRepositoryPort {
    void registrar(Audit audit);
    Page<Audit> list(Pageable pageable);
    Page<Audit> showAudits(
            LocalDate startDate,
            LocalDate endDate,
            AuditAction action,
            Pageable pageable
    );
    List<Audit> listByPeriod(
            String fechaInicio,
            String fechaFin
    );
}


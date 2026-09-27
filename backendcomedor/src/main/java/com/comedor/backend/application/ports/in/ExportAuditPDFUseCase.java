package com.comedor.backend.application.ports.in;

import java.time.LocalDate;

public interface ExportAuditPDFUseCase {
    byte[] exportar(LocalDate fechaInicio, LocalDate fechaFin);

}

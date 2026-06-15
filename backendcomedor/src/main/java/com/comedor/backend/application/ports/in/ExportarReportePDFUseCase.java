package com.comedor.backend.application.ports.in;

import java.time.LocalDate;

public interface ExportarReportePDFUseCase {
    byte[] exportar(int reporteId);
    public byte[] exportar(LocalDate startDate, LocalDate endDate);
}

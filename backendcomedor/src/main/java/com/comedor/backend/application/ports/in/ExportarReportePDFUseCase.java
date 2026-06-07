package com.comedor.backend.application.ports.in;

public interface ExportarReportePDFUseCase {
    byte[] exportar(int reporteId);
}

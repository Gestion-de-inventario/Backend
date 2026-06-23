package com.comedor.backend.application.ports.in;

public interface ExportReportExcelUseCase {
    byte[] exportar(int reporteId);
}

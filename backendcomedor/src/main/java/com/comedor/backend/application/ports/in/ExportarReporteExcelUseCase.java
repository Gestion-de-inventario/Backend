package com.comedor.backend.application.ports.in;

public interface ExportarReporteExcelUseCase {
    byte[] exportar(int reporteId);
}

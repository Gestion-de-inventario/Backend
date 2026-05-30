package com.comedor.backend.application.ports.in;

import com.comedor.backend.infrastructure.adapters.in.web.dto.response.DetalleReporteMenuResponseDTO;

import java.time.LocalDate;

public interface ObtenerReporteMenuPorFechaUseCase {
    DetalleReporteMenuResponseDTO obtenerPorFecha(LocalDate fecha);
}

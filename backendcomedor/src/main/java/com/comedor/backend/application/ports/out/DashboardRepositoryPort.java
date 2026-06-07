package com.comedor.backend.application.ports.out;

import com.comedor.backend.infrastructure.adapters.in.web.dto.request.ProductoRotacionDTO;
import com.comedor.backend.infrastructure.adapters.in.web.dto.response.DashboardResponseDTO;
import com.comedor.backend.infrastructure.adapters.in.web.dto.response.ResumenMensualDTO;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public interface DashboardRepositoryPort {
    // CA 1
    List<ProductoRotacionDTO> obtenerTop5ProductosMasRotados(LocalDate inicio, LocalDate fin);

    // CA 2 y 3
    DashboardResponseDTO obtenerConsolidadoFinanciero(int anio, int mes, List<ProductoRotacionDTO> topProductos);
}

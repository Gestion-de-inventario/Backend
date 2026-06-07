package com.comedor.backend.application.ports.out;

import com.comedor.backend.infrastructure.adapters.in.web.dto.request.ProductoRotacionDTO;
import com.comedor.backend.infrastructure.adapters.in.web.dto.request.ResumenMensualDTO;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public interface DashboardRepositoryPort {
    List<ProductoRotacionDTO> obtenerTop5ProductosMasRotados(LocalDate inicio, LocalDate fin);
    BigDecimal obtenerValorTotalInventario();
    List<ResumenMensualDTO> obtenerGastosConsolidadosMensuales(int anio, int mes);
}

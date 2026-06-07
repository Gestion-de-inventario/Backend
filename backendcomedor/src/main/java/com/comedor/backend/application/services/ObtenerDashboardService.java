package com.comedor.backend.application.services;

import com.comedor.backend.application.ports.in.ObtenerDashboardUseCase;
import com.comedor.backend.application.ports.out.DashboardRepositoryPort;
import com.comedor.backend.infrastructure.adapters.in.web.dto.response.DashboardResponseDTO;
import com.comedor.backend.infrastructure.adapters.in.web.dto.request.ProductoRotacionDTO;
import com.comedor.backend.infrastructure.adapters.in.web.dto.response.ResumenMensualDTO;
import org.springframework.cache.annotation.Cacheable;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public class ObtenerDashboardService implements ObtenerDashboardUseCase {

    private final DashboardRepositoryPort dashboardRepositoryPort;

    public ObtenerDashboardService(DashboardRepositoryPort dashboardRepositoryPort) {
        this.dashboardRepositoryPort = dashboardRepositoryPort;
    }

    @Override
    @Cacheable(value = "dashboardCache", key = "#anio + '-' + #mes")
    public DashboardResponseDTO ejecutar(int anio, int mes) {
        LocalDate inicio = LocalDate.of(anio, mes, 1);
        LocalDate fin = inicio.withDayOfMonth(inicio.lengthOfMonth());

        // 1. Obtener la rotación de productos
        List<ProductoRotacionDTO> topProductos = dashboardRepositoryPort.obtenerTop5ProductosMasRotados(inicio, fin);

        // 2. Obtener el consolidado financiero (Tarjetas y gráfico diario)
        return dashboardRepositoryPort.obtenerConsolidadoFinanciero(anio, mes, topProductos);
    }
}

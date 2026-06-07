package com.comedor.backend.application.services;

import com.comedor.backend.application.ports.in.ObtenerDashboardUseCase;
import com.comedor.backend.application.ports.out.DashboardRepositoryPort;
import com.comedor.backend.infrastructure.adapters.in.web.dto.request.DashboardResponseDTO;
import com.comedor.backend.infrastructure.adapters.in.web.dto.request.ProductoRotacionDTO;
import com.comedor.backend.infrastructure.adapters.in.web.dto.request.ResumenMensualDTO;
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
        // Calcular rangos de fechas para el mes solicitado
        LocalDate inicio = LocalDate.of(anio, mes, 1);
        LocalDate fin = inicio.withDayOfMonth(inicio.lengthOfMonth());

        // Consultar los datos a través del puerto de salida
        List<ProductoRotacionDTO> topProductos = dashboardRepositoryPort.obtenerTop5ProductosMasRotados(inicio, fin);
        BigDecimal valorInventario = dashboardRepositoryPort.obtenerValorTotalInventario();
        List<ResumenMensualDTO> resumenGastos = dashboardRepositoryPort.obtenerGastosConsolidadosMensuales(anio, mes);

        // Retornar el DTO consolidado que se guardará en caché
        return new DashboardResponseDTO(topProductos, valorInventario, resumenGastos);
    }
}

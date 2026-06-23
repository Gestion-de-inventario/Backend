package com.comedor.backend.application.services;

import com.comedor.backend.application.ports.in.GetDashboardUseCase;
import com.comedor.backend.application.ports.out.DashboardRepositoryPort;
import com.comedor.backend.infrastructure.adapters.in.web.dto.response.DashboardResponseDTO;
import com.comedor.backend.infrastructure.adapters.in.web.dto.request.ProductRotationDTO;

import java.time.LocalDate;
import java.util.List;

public class GetDashboardService implements GetDashboardUseCase {

    private final DashboardRepositoryPort dashboardRepositoryPort;

    public GetDashboardService(DashboardRepositoryPort dashboardRepositoryPort) {
        this.dashboardRepositoryPort = dashboardRepositoryPort;
    }

    @Override
    public DashboardResponseDTO ejecutar(int anio, int mes) {
        LocalDate inicio = LocalDate.of(anio, mes, 1);
        LocalDate fin = inicio.withDayOfMonth(inicio.lengthOfMonth());

        // 1. Obtener la rotación de productos
        List<ProductRotationDTO> topProductos = dashboardRepositoryPort.obtenerTop5ProductosMasRotados(inicio, fin);

        // 2. Obtener el consolidado financiero (Tarjetas y gráfico diario)
        return dashboardRepositoryPort.obtenerConsolidadoFinanciero(anio, mes, topProductos);
    }
}

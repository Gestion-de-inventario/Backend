package com.comedor.backend.infrastructure.adapters.out.persistence;

import com.comedor.backend.application.ports.out.DashboardRepositoryPort;
import com.comedor.backend.infrastructure.adapters.in.web.dto.request.ProductoRotacionDTO;
import com.comedor.backend.infrastructure.adapters.in.web.dto.request.ResumenMensualDTO;
import com.comedor.backend.infrastructure.adapters.out.persistence.repository.DashboardJpaRepository;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Component
public class DashboardRepositoryAdapter implements DashboardRepositoryPort {

    private final DashboardJpaRepository dashboardJpaRepository;

    public DashboardRepositoryAdapter(DashboardJpaRepository dashboardJpaRepository) {
        this.dashboardJpaRepository = dashboardJpaRepository;
    }

    @Override
    public List<ProductoRotacionDTO> obtenerTop5ProductosMasRotados(LocalDate inicio, LocalDate fin) {
        // Convertimos LocalDate del dominio a LocalDateTime para la base de datos
        LocalDateTime inicioDateTime = inicio.atStartOfDay();
        LocalDateTime finDateTime = fin.atTime(23, 59, 59);

        List<ProductoRotacionDTO> resultado = dashboardJpaRepository.findTop5ProductosMasRotados(inicioDateTime, finDateTime);

        // Nos aseguramos de devolver como máximo 5 elementos
        return resultado.size() > 5 ? resultado.subList(0, 5) : resultado;
    }

    @Override
    public BigDecimal obtenerValorTotalInventario() {
        BigDecimal total = dashboardJpaRepository.findValorTotalInventario();
        return total != null ? total : BigDecimal.ZERO;
    }

    @Override
    public List<ResumenMensualDTO> obtenerGastosConsolidadosMensuales(int anio, int mes) {
        return dashboardJpaRepository.findGastosConsolidadosMensuales(anio, mes);
    }
}

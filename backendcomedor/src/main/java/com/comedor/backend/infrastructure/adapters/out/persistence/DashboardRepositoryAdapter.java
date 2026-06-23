package com.comedor.backend.infrastructure.adapters.out.persistence;

import com.comedor.backend.application.ports.out.DashboardRepositoryPort;
import com.comedor.backend.infrastructure.adapters.in.web.dto.request.ProductRotationDTO;
import com.comedor.backend.infrastructure.adapters.in.web.dto.response.DashboardResponseDTO;
import com.comedor.backend.infrastructure.adapters.in.web.dto.response.ResumenMensualDTO;
import com.comedor.backend.infrastructure.adapters.out.persistence.repository.DashboardJpaRepository;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

@Component
public class DashboardRepositoryAdapter implements DashboardRepositoryPort {

    private final DashboardJpaRepository dashboardJpaRepository;

    public DashboardRepositoryAdapter(DashboardJpaRepository dashboardJpaRepository) {
        this.dashboardJpaRepository = dashboardJpaRepository;
    }

    @Override
    public List<ProductRotationDTO> obtenerTop5ProductosMasRotados(LocalDate inicio, LocalDate fin) {
        LocalDateTime inicioDateTime = inicio.atStartOfDay();
        LocalDateTime finDateTime = fin.atTime(23, 59, 59);
        List<ProductRotationDTO> resultado = dashboardJpaRepository.findTop5ProductosMasRotados(inicioDateTime, finDateTime);
        return resultado.size() > 5 ? resultado.subList(0, 5) : resultado;
    }

    @Override
    public DashboardResponseDTO obtenerConsolidadoFinanciero(int anio, int mes, List<ProductRotationDTO> topProductos) {
        Map<String, ResumenMensualDTO> mapaDiario = new TreeMap<>(); 

        BigDecimal totalIngresos = BigDecimal.ZERO;
        BigDecimal totalEgresos = BigDecimal.ZERO;

        // 1. Procesar Reportes de Menú (Ingresos y Egresos de preparación)
        List<Object[]> menuSummary = dashboardJpaRepository.findDailyMenuReportSummary(anio, mes);
        for (Object[] row : menuSummary) {
            String fecha = row[0].toString();
            BigDecimal ingresos = row[1] != null ? (BigDecimal) row[1] : BigDecimal.ZERO;
            BigDecimal egresos = row[2] != null ? (BigDecimal) row[2] : BigDecimal.ZERO;

            totalIngresos = totalIngresos.add(ingresos);
            totalEgresos = totalEgresos.add(egresos);

            ResumenMensualDTO dto = new ResumenMensualDTO(fecha, ingresos, egresos, ingresos.subtract(egresos));
            mapaDiario.put(fecha, dto);
        }

        // 2. Procesar Órdenes de Compra (Egresos por compras)
        List<Object[]> purchaseSummary = dashboardJpaRepository.findDailyPurchaseSummary(anio, mes);
        for (Object[] row : purchaseSummary) {
            String fecha = row[0].toString();
            BigDecimal egresosCompra = row[1] != null ? (BigDecimal) row[1] : BigDecimal.ZERO;

            totalEgresos = totalEgresos.add(egresosCompra);

            if (mapaDiario.containsKey(fecha)) {
                ResumenMensualDTO existente = mapaDiario.get(fecha);
                existente.setEgresosDiarios(existente.getEgresosDiarios().add(egresosCompra));
                existente.setNetoDiario(existente.getIngresosDiarios().subtract(existente.getEgresosDiarios()));
            } else {
                ResumenMensualDTO dto = new ResumenMensualDTO(fecha, BigDecimal.ZERO, egresosCompra, BigDecimal.ZERO.subtract(egresosCompra));
                mapaDiario.put(fecha, dto);
            }
        }

        BigDecimal balanceNeto = totalIngresos.subtract(totalEgresos);
        List<ResumenMensualDTO> resumenLista = new ArrayList<>(mapaDiario.values());

        return new DashboardResponseDTO(topProductos, totalIngresos, totalEgresos, balanceNeto, resumenLista);
    }
}

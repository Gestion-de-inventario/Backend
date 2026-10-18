package com.comedor.backend.infrastructure.adapters.in.web;

import com.comedor.backend.application.common.mapper.ProductMapper;
import com.comedor.backend.application.ports.in.*;
import com.comedor.backend.infrastructure.adapters.in.web.dto.response.*;
import com.comedor.backend.infrastructure.adapters.in.web.mapper.DashboardWebMapper;
import com.comedor.backend.infrastructure.config.PeruTime;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final GetDashboardUseCase getDashboardUseCase;
    private final GetTopHighStockProductsUseCase getTopHighStockProductsUseCase;
    private final GetTopLowStockProductsUseCase getTopLowStockProductsUseCase;
    private final GetTopConsumersUseCase getTopConsumersUseCase;
    private final GetWeeklySoldMenusUseCase getWeeklySoldMenusUseCase;
    private final GetYearlyFinancialSummaryUseCase getYearlyFinancialSummaryUseCase;
    private final ProductMapper productMapper;
    private final DashboardWebMapper dashboardWebMapper;

    @PreAuthorize("hasAuthority('DASHBOARD_VIEW')")
    @GetMapping
    public ResponseEntity<DashboardResponseDTO> getDashboard(
            @RequestParam(value = "anio", required = false) Integer anio,
            @RequestParam(value = "mes", required = false) Integer mes) {

        int añoBusqueda = (anio != null) ? anio : PeruTime.today().getYear();
        int mesBusqueda = (mes != null) ? mes : PeruTime.today().getMonthValue();

        DashboardResponseDTO response = getDashboardUseCase.ejecutar(añoBusqueda, mesBusqueda);
        return ResponseEntity.ok(response);
    }

    @PreAuthorize("hasAuthority('DASHBOARD_VIEW')")
    @GetMapping("/top-high-stock")
    public ResponseEntity<List<ProductResponseDTO>> getTopHighStockProducts() {
        List<ProductResponseDTO> response = productMapper.toListProductoResponseDTO(getTopHighStockProductsUseCase.getTopHighStock());
        return ResponseEntity.ok(response);
    }

    @PreAuthorize("hasAuthority('DASHBOARD_VIEW')")
    @GetMapping("/top-low-stock")
    public ResponseEntity<List<ProductResponseDTO>> getTopLowStockProducts() {
        List<ProductResponseDTO> response = productMapper.toListProductoResponseDTO(getTopLowStockProductsUseCase.getTopLowStock());
        return ResponseEntity.ok(response);
    }

    @PreAuthorize("hasAuthority('DASHBOARD_VIEW')")
    @GetMapping("/top-consumers")
    public ResponseEntity<List<TopBeneficiaryResponseDTO>> getTopConsumers(
            @RequestParam(value = "startDate", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(value = "endDate", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {

        // Valores por defecto: Mes actual si el cliente no envía rango
        LocalDate inicio = (startDate != null) ? startDate : PeruTime.today().withDayOfMonth(1);
        LocalDate fin = (endDate != null) ? endDate : PeruTime.today();

        var consumers = getTopConsumersUseCase.getTopConsumers(inicio, fin);
        return ResponseEntity.ok(dashboardWebMapper.toTopBeneficiaryResponseDTOList(consumers));
    }

    @PreAuthorize("hasAuthority('DASHBOARD_VIEW')")
    @GetMapping("/yearly-financial")
    public ResponseEntity<List<YearlyFinancialResponseDTO>> getYearlyFinancialSummary(
            @RequestParam(value = "year", required = false) Integer year) {

        int anioBusqueda = (year != null) ? year : PeruTime.today().getYear();
        var summary = getYearlyFinancialSummaryUseCase.getSummaryByYear(anioBusqueda);
        return ResponseEntity.ok(dashboardWebMapper.toYearlyFinancialResponseDTOList(summary));
    }

    @PreAuthorize("hasAuthority('DASHBOARD_VIEW')")
    @GetMapping("/weekly-sales")
    public ResponseEntity<List<WeeklySoldMenusResponseDTO>> getWeeklySoldMenus(
            @RequestParam(value = "date", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {

        LocalDate fechaReferencia = (date != null) ? date : PeruTime.today();
        var weeklyData = getWeeklySoldMenusUseCase.getWeeklySoldMenus(fechaReferencia);
        return ResponseEntity.ok(dashboardWebMapper.toWeeklySoldMenusResponseDTOList(weeklyData));
    }
}

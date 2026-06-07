package com.comedor.backend.infrastructure.adapters.in.web;

import com.comedor.backend.application.ports.in.ObtenerDashboardUseCase;
import com.comedor.backend.infrastructure.adapters.in.web.dto.response.DashboardResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

@RestController
@RequestMapping("/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final ObtenerDashboardUseCase obtenerDashboardUseCase;

    @PreAuthorize("hasAuthority('DASHBOARD_VIEW')")
    @GetMapping
    public ResponseEntity<DashboardResponseDTO> getDashboard(
            @RequestParam(value = "anio", required = false) Integer anio,
            @RequestParam(value = "mes", required = false) Integer mes) {

        int añoBusqueda = (anio != null) ? anio : LocalDate.now().getYear();
        int mesBusqueda = (mes != null) ? mes : LocalDate.now().getMonthValue();

        DashboardResponseDTO response = obtenerDashboardUseCase.ejecutar(añoBusqueda, mesBusqueda);
        return ResponseEntity.ok(response);
    }
}

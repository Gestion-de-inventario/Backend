package com.comedor.backend.infrastructure.adapters.in.web;

import com.comedor.backend.application.ports.in.ObtenerDashboardUseCase;
import com.comedor.backend.infrastructure.adapters.in.web.dto.request.DashboardResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

@RestController
@RequestMapping("/dashboard")
public class DashboardController {

    private final ObtenerDashboardUseCase obtenerDashboardUseCase;

    public DashboardController(ObtenerDashboardUseCase obtenerDashboardUseCase) {
        this.obtenerDashboardUseCase = obtenerDashboardUseCase;
    }

    @GetMapping
    public ResponseEntity<DashboardResponseDTO> getDashboard(
            @RequestParam(value = "anio", required = false) Integer anio,
            @RequestParam(value = "mes", required = false) Integer mes) {

        // Si el frontend no envía el mes o año, usamos el mes y año actuales por defecto
        int añoBusqueda = (anio != null) ? anio : LocalDate.now().getYear();
        int mesBusqueda = (mes != null) ? mes : LocalDate.now().getMonthValue();

        DashboardResponseDTO response = obtenerDashboardUseCase.ejecutar(añoBusqueda, mesBusqueda);
        return ResponseEntity.ok(response);
    }
}

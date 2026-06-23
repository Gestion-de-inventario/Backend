package com.comedor.backend.infrastructure.adapters.in.web;

import com.comedor.backend.application.ports.in.GetDashboardUseCase;
import com.comedor.backend.infrastructure.adapters.in.web.dto.response.DashboardResponseDTO;
import com.comedor.backend.infrastructure.config.PeruTime;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final GetDashboardUseCase getDashboardUseCase;

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
}

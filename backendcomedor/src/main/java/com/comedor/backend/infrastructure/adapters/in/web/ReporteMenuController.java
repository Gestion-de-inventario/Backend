package com.comedor.backend.infrastructure.adapters.in.web;

import com.comedor.backend.application.services.CrearReporteMenuService;
import com.comedor.backend.infrastructure.adapters.in.web.dto.request.ReporteMenuRequestDTO;
import com.comedor.backend.infrastructure.adapters.in.web.dto.response.ReporteMenuResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/reporte-menu")
@RequiredArgsConstructor
public class ReporteMenuController {
    private final CrearReporteMenuService crearReporteMenuService;

    @PreAuthorize("hasAnyRole('PRESIDENTA', 'SOCIA')")
    @PostMapping("/create")
    public ReporteMenuResponseDTO createUser(@RequestBody ReporteMenuRequestDTO reporteMenuRequestDTO) {
        return crearReporteMenuService.crearReporteMenu(reporteMenuRequestDTO);
    }
}

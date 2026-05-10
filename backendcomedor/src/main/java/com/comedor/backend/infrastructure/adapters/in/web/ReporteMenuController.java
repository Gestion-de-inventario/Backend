package com.comedor.backend.infrastructure.adapters.in.web;

import com.comedor.backend.application.ports.in.AgregarRegistroProductoUseCase;
import com.comedor.backend.application.services.CrearReporteMenuService;
import com.comedor.backend.infrastructure.adapters.in.web.dto.request.RegistroProductoRequestDTO;
import com.comedor.backend.infrastructure.adapters.in.web.dto.request.ReporteMenuRequestDTO;
import com.comedor.backend.infrastructure.adapters.in.web.dto.response.RegistroProductoResponseDTO;
import com.comedor.backend.infrastructure.adapters.in.web.dto.response.ReporteMenuResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/reporte-menu")
@RequiredArgsConstructor
public class ReporteMenuController {
    private final CrearReporteMenuService crearReporteMenuService;
    private final AgregarRegistroProductoUseCase agregarRegistroProductoUseCase;

    @PreAuthorize("hasAnyRole('PRESIDENTA', 'SOCIA')")
    @PostMapping("/create")
    public ReporteMenuResponseDTO createReporteMenu(@RequestBody ReporteMenuRequestDTO reporteMenuRequestDTO) {
        return crearReporteMenuService.crearReporteMenu(reporteMenuRequestDTO);
    }

    @PreAuthorize("hasAnyRole('PRESIDENTA', 'SOCIA')")
    @PostMapping("/{id}/registros")
    public RegistroProductoResponseDTO agregarRegistroProducto(@PathVariable int id, @RequestBody RegistroProductoRequestDTO registroProductoRequestDTO) {
        return agregarRegistroProductoUseCase.agregarRegistroProducto(id, registroProductoRequestDTO);
    }
}

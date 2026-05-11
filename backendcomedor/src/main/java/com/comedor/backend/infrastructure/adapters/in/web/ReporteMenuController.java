package com.comedor.backend.infrastructure.adapters.in.web;

import com.comedor.backend.application.ports.in.*;
import com.comedor.backend.application.services.CrearReporteMenuService;
import com.comedor.backend.application.services.EditarRegistroBeneficiarioService;
import com.comedor.backend.infrastructure.adapters.in.web.dto.request.ControlBeneficiarioRequestDTO;
import com.comedor.backend.infrastructure.adapters.in.web.dto.request.RegistroProductoRequestDTO;
import com.comedor.backend.infrastructure.adapters.in.web.dto.request.ReporteMenuRequestDTO;
import com.comedor.backend.infrastructure.adapters.in.web.dto.response.RegistroBeneficiarioResponseDTO;
import com.comedor.backend.infrastructure.adapters.in.web.dto.response.RegistroProductoResponseDTO;
import com.comedor.backend.infrastructure.adapters.in.web.dto.response.ReporteMenuResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/reporte-menu")
@RequiredArgsConstructor
public class ReporteMenuController {
    private final CrearReporteMenuService crearReporteMenuService;
    private final AgregarRegistroProductoUseCase agregarRegistroProductoUseCase;
    private final AgregarRegistroBeneficiarioUseCase agregarRegistroBeneficiarioUseCase;
    private final EliminarRegistroProductoUseCase eliminarRegistroProductoUseCase;
    private final EliminarRegistroBeneficiarioUseCase eliminarRegistroBeneficiarioUseCase;
    private final EditarRegistroProductoUseCase editarRegistroProductoUseCase;
    private final EditarRegistroBeneficiarioUseCase editarRegistroBeneficiarioUseCase;

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

    @PreAuthorize("hasAnyRole('PRESIDENTA', 'SOCIA')")
    @PatchMapping("/{reporteId}/registros/{registroId}")
    public RegistroProductoResponseDTO editarRegistroProducto(
            @PathVariable int reporteId,
            @PathVariable int registroId,
            @RequestBody RegistroProductoRequestDTO dto
    ) {
        return editarRegistroProductoUseCase.editarRegistroProducto(reporteId, registroId, dto);
    }

    @PreAuthorize("hasAnyRole('PRESIDENTA', 'SOCIA')")
    @DeleteMapping("/{reporteId}/registros/{registroId}")
    public ResponseEntity<Void> eliminarRegistroProducto(
            @PathVariable int reporteId,
            @PathVariable int registroId
    ) {

        eliminarRegistroProductoUseCase.eliminarRegistroProducto(reporteId, registroId);

        return ResponseEntity.noContent().build();
    }

    @PreAuthorize("hasAnyRole('PRESIDENTA', 'SOCIA')")
    @PostMapping("/{id}/beneficiarios")
    public RegistroBeneficiarioResponseDTO agregarBeneficiario(
            @PathVariable int id,
            @RequestBody ControlBeneficiarioRequestDTO dto
    ) {
        return agregarRegistroBeneficiarioUseCase.agregarRegistroBeneficiario(id,dto);
    }

    @PreAuthorize("hasAnyRole('PRESIDENTA', 'SOCIA')")
    @PatchMapping("/{reporteId}/beneficiarios/{controlId}")
    public RegistroBeneficiarioResponseDTO editarBeneficiario(
            @PathVariable int reporteId,
            @PathVariable int controlId,
            @RequestBody ControlBeneficiarioRequestDTO dto
    ) {
        return editarRegistroBeneficiarioUseCase.editarRegistroBeneficiario(reporteId,controlId,dto);
    }

    @PreAuthorize("hasAnyRole('PRESIDENTA', 'SOCIA')")
    @DeleteMapping("/{reporteId}/beneficiarios/{controlId}")
    public ResponseEntity<Void> eliminarBeneficiario(
            @PathVariable int reporteId,
            @PathVariable int controlId
    ) {

        eliminarRegistroBeneficiarioUseCase.eliminarRegistroBeneficiario(reporteId,controlId);
        return ResponseEntity.noContent().build();
    }

}

package com.comedor.backend.infrastructure.adapters.in.web;

import com.comedor.backend.application.ports.in.*;
import com.comedor.backend.application.services.CrearReporteMenuService;
import com.comedor.backend.infrastructure.adapters.in.web.dto.request.ControlBeneficiarioRequestDTO;
import com.comedor.backend.infrastructure.adapters.in.web.dto.request.RegistroProductoRequestDTO;
import com.comedor.backend.infrastructure.adapters.in.web.dto.request.ReporteMenuRequestDTO;
import com.comedor.backend.infrastructure.adapters.in.web.dto.response.*;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/menu_report")
@RequiredArgsConstructor
public class MenuRecordController {
    private final CrearReporteMenuService crearReporteMenuService;
    private final AgregarRegistroProductoUseCase agregarRegistroProductoUseCase;
    private final AgregarRegistroBeneficiarioUseCase agregarRegistroBeneficiarioUseCase;
    private final EliminarRegistroProductoUseCase eliminarRegistroProductoUseCase;
    private final EliminarRegistroBeneficiarioUseCase eliminarRegistroBeneficiarioUseCase;
    private final EditarRegistroProductoUseCase editarRegistroProductoUseCase;
    private final EditarRegistroBeneficiarioUseCase editarRegistroBeneficiarioUseCase;
    private final ObtenerReporteMenuPorFechaUseCase obtenerReporteMenuPorFechaUseCase;
    private final ObtenerResumenReporteMenuUseCase obtenerResumenReporteMenuUseCase;

    @PreAuthorize("hasAnyRole('PRESIDENTA', 'SOCIA')")
    @PostMapping("/create")
    public ReporteMenuResponseDTO createReporteMenu(@RequestBody ReporteMenuRequestDTO reporteMenuRequestDTO) {
        return crearReporteMenuService.crearReporteMenu(reporteMenuRequestDTO);
    }

    @GetMapping("/date/{fecha}")
    public DetalleReporteMenuResponseDTO obtenerPorFecha(
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha){
        return obtenerReporteMenuPorFechaUseCase.obtenerPorFecha(fecha);
    }

    @PreAuthorize("hasAnyRole('PRESIDENTA', 'SOCIA')")
    @PostMapping("/{id}/records")
    public RegistroProductoResponseDTO agregarRegistroProducto(@PathVariable int id, @RequestBody RegistroProductoRequestDTO registroProductoRequestDTO) {
        return agregarRegistroProductoUseCase.agregarRegistroProducto(id, registroProductoRequestDTO);
    }

    @PreAuthorize("hasAnyRole('PRESIDENTA', 'SOCIA')")
    @PatchMapping("/{reporteId}/records/{registroId}")
    public RegistroProductoResponseDTO editarRegistroProducto(
            @PathVariable int reporteId,
            @PathVariable int registroId,
            @RequestBody RegistroProductoRequestDTO dto
    ) {
        return editarRegistroProductoUseCase.editarRegistroProducto(reporteId, registroId, dto);
    }

    @PreAuthorize("hasAnyRole('PRESIDENTA', 'SOCIA')")
    @DeleteMapping("/{reporteId}/records/{registroId}")
    public ResponseEntity<Void> eliminarRegistroProducto(
            @PathVariable int reporteId,
            @PathVariable int registroId
    ) {

        eliminarRegistroProductoUseCase.eliminarRegistroProducto(reporteId, registroId);

        return ResponseEntity.noContent().build();
    }

    @PreAuthorize("hasAnyRole('PRESIDENTA', 'SOCIA')")
    @PostMapping("/{id}/beneficiaries")
    public RegistroBeneficiarioResponseDTO agregarBeneficiario(
            @PathVariable int id,
            @RequestBody ControlBeneficiarioRequestDTO dto
    ) {
        return agregarRegistroBeneficiarioUseCase.agregarRegistroBeneficiario(id,dto);
    }

    @PreAuthorize("hasAnyRole('PRESIDENTA', 'SOCIA')")
    @PatchMapping("/{reporteId}/beneficiaries/{controlId}")
    public RegistroBeneficiarioResponseDTO editarBeneficiario(
            @PathVariable int reporteId,
            @PathVariable int controlId,
            @RequestBody ControlBeneficiarioRequestDTO dto
    ) {
        return editarRegistroBeneficiarioUseCase.editarRegistroBeneficiario(reporteId,controlId,dto);
    }

    @PreAuthorize("hasAnyRole('PRESIDENTA', 'SOCIA')")
    @DeleteMapping("/{reporteId}/beneficiaries/{controlId}")
    public ResponseEntity<Void> eliminarBeneficiario(
            @PathVariable int reporteId,
            @PathVariable int controlId
    ) {

        eliminarRegistroBeneficiarioUseCase.eliminarRegistroBeneficiario(reporteId,controlId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/summary")
    @PreAuthorize("hasAnyRole('PRESIDENTA', 'SOCIA')")
    public ResumenReporteMenuResponseDTO obtenerResumen(@PathVariable int id) {
        return obtenerResumenReporteMenuUseCase.obtenerResumen(id);
    }

}

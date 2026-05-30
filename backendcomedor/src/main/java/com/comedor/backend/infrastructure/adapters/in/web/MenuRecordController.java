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

    private final CrearReporteMenuUseCase crearReporteMenuUseCase;
    private final AgregarRegistroBeneficiarioUseCase agregarRegistroBeneficiarioUseCase;
    private final EliminarRegistroBeneficiarioUseCase eliminarRegistroBeneficiarioUseCase;
    private final EditarRegistroBeneficiarioUseCase editarRegistroBeneficiarioUseCase;
    private final ObtenerReporteMenuPorFechaUseCase obtenerReporteMenuPorFechaUseCase;
    private final ObtenerResumenReporteMenuUseCase obtenerResumenReporteMenuUseCase;

    @PreAuthorize("hasAuthority('MENU_REPORT_CREATE_REPORT')")
    @PostMapping("/create")
    public ReporteMenuResponseDTO createReporteMenu(@RequestBody ReporteMenuRequestDTO request) {
        return crearReporteMenuUseCase.crearReporteMenu(request);
    }

    @PreAuthorize("hasAuthority('MENU_REPORT_GET_BY_DATE')")
    @GetMapping("/date/{fecha}")
    public DetalleReporteMenuResponseDTO obtenerPorFecha(
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha) {
        return obtenerReporteMenuPorFechaUseCase.obtenerPorFecha(fecha);
    }

    @PreAuthorize("hasAuthority('MENU_REPORT_ADD_BENEFICIARY')")
    @PostMapping("/{id}/beneficiaries")
    public RegistroBeneficiarioResponseDTO agregarBeneficiario(
            @PathVariable int id,
            @RequestBody ControlBeneficiarioRequestDTO dto) {
        return agregarRegistroBeneficiarioUseCase.agregarRegistroBeneficiario(id, dto);
    }

    @PreAuthorize("hasAuthority('MENU_REPORT_EDIT_BENEFICIARY')")
    @PatchMapping("/{reporteId}/beneficiaries/{controlId}")
    public RegistroBeneficiarioResponseDTO editarBeneficiario(
            @PathVariable int reporteId,
            @PathVariable int controlId,
            @RequestBody ControlBeneficiarioRequestDTO dto) {
        return editarRegistroBeneficiarioUseCase.editarRegistroBeneficiario(reporteId, controlId, dto);
    }

    @PreAuthorize("hasAuthority('MENU_REPORT_REMOVE_BENEFICIARY')")
    @DeleteMapping("/{reporteId}/beneficiaries/{controlId}")
    public ResponseEntity<Void> eliminarBeneficiario(
            @PathVariable int reporteId,
            @PathVariable int controlId) {
        eliminarRegistroBeneficiarioUseCase.eliminarRegistroBeneficiario(reporteId, controlId);
        return ResponseEntity.noContent().build();
    }

    @PreAuthorize("hasAuthority('MENU_REPORT_GET_SUMMARY')")
    @GetMapping("/{id}/summary")
    public ResumenReporteMenuResponseDTO obtenerResumen(@PathVariable int id) {
        return obtenerResumenReporteMenuUseCase.obtenerResumen(id);
    }
}

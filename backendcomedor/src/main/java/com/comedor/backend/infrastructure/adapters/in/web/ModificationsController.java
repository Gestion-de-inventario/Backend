package com.comedor.backend.infrastructure.adapters.in.web;

import com.comedor.backend.application.ports.in.ExportarModificacionesPDFUseCase;
import com.comedor.backend.application.ports.in.ListarModificacionesUseCase;
import com.comedor.backend.infrastructure.adapters.in.web.dto.response.ModificationsResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

@RestController
@RequestMapping("/modifications")
@RequiredArgsConstructor
public class ModificationsController {

    private final ListarModificacionesUseCase listarModificacionesUseCase;
    private final ExportarModificacionesPDFUseCase exportarModificacionesPDFUseCase;


    @PreAuthorize("hasAuthority('MODIFICATION_LIST_ALL')")
    @GetMapping
    public ResponseEntity<Page<ModificationsResponseDTO>> list(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaInicio,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaFin
    ) {
        return ResponseEntity.ok(listarModificacionesUseCase.list(page, size, fechaInicio, fechaFin));
    }

    @PreAuthorize("hasAuthority('MODIFICATION_LIST_ALL')")
    @GetMapping("/export/pdf")
    public ResponseEntity<byte[]> exportarPDF(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaInicio,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaFin
    ) {
        byte[] pdf = exportarModificacionesPDFUseCase.exportar(fechaInicio, fechaFin);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=modificaciones.pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdf);
    }
}

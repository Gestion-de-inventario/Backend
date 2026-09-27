package com.comedor.backend.infrastructure.adapters.in.web;

import com.comedor.backend.application.ports.in.ExportAuditPDFUseCase;
import com.comedor.backend.application.ports.in.ListAuditUseCase;
import com.comedor.backend.domain.model.enums.AuditAction;
import com.comedor.backend.infrastructure.adapters.in.web.dto.response.AuditResponseDTO;
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
@RequestMapping("/audits")
@RequiredArgsConstructor
public class AuditController {

    private final ListAuditUseCase listModificationsUseCase;
    private final ExportAuditPDFUseCase exportAuditPDFUseCase;


    @PreAuthorize("hasAuthority('AUDIT_LIST_ALL')")
    @GetMapping
    public ResponseEntity<Page<AuditResponseDTO>> list(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaInicio,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaFin,
            @RequestParam(required = false) AuditAction action
    ) {
        return ResponseEntity.ok(listModificationsUseCase.list(page, size, fechaInicio, fechaFin,action));
    }

    @PreAuthorize("hasAuthority('AUDIT_LIST_ALL')")
    @GetMapping("/export/pdf")
    public ResponseEntity<byte[]> exportarPDF(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaInicio,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaFin
    ) {
        byte[] pdf = exportAuditPDFUseCase.exportar(fechaInicio, fechaFin);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=modificaciones.pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdf);
    }
}

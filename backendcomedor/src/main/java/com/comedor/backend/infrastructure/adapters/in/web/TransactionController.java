package com.comedor.backend.infrastructure.adapters.in.web;

import com.comedor.backend.application.ports.in.ExportarTransaccionesPDFUseCase;
import com.comedor.backend.application.ports.in.ListarTransaccionesUseCase;
import com.comedor.backend.domain.model.enums.FuenteTransaccion;
import com.comedor.backend.domain.model.enums.TipoMovimiento;
import com.comedor.backend.infrastructure.adapters.in.web.dto.response.TransaccionResponseDTO;
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
import java.util.List;

@RestController
@RequestMapping("/transactions")
@RequiredArgsConstructor
public class TransactionController {
    private final ListarTransaccionesUseCase listarTransaccionesUseCase;
    private final ExportarTransaccionesPDFUseCase exportarTransaccionesPDFUseCase;

    @PreAuthorize("hasAuthority('TRANSACTION_LIST_ALL')")
    @GetMapping
    public Page<TransaccionResponseDTO> list(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaInicio,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaFin,
            @RequestParam(required = false) TipoMovimiento type,
            @RequestParam(required = false) FuenteTransaccion source,
            @RequestParam(required = false) String productName
    ) {
        return listarTransaccionesUseCase.list(page, size, fechaInicio, fechaFin,type, source, productName);
    }

    @PreAuthorize("hasAuthority('TRANSACTION_LIST_ALL')")
    @GetMapping("/export/pdf")
    public ResponseEntity<byte[]> exportarPDF(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaInicio,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaFin
    ) {
        byte[] pdf = exportarTransaccionesPDFUseCase.exportar(fechaInicio, fechaFin);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=transacciones.pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdf);
    }

}

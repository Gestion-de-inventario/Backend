package com.comedor.backend.infrastructure.adapters.in.web;

import com.comedor.backend.application.ports.in.ExportTransactionsPDFUseCase;
import com.comedor.backend.application.ports.in.ListTransactionsUseCase;
import com.comedor.backend.domain.model.enums.TransactionReferenceType;
import com.comedor.backend.domain.model.enums.TransactionSource;
import com.comedor.backend.domain.model.enums.MovementType;
import com.comedor.backend.infrastructure.adapters.in.web.dto.response.TransactionResponseDTO;
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
@RequestMapping("/transactions")
@RequiredArgsConstructor
public class TransactionController {
    private final ListTransactionsUseCase listTransactionsUseCase;
    private final ExportTransactionsPDFUseCase exportTransactionsPDFUseCase;

    @PreAuthorize("hasAuthority('TRANSACTION_LIST_ALL')")
    @GetMapping
    public Page<TransactionResponseDTO> list(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaInicio,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaFin,
            @RequestParam(required = false) MovementType type,
            @RequestParam(required = false) TransactionSource source,
            @RequestParam(required = false) String productName,
            @RequestParam(required = false) TransactionReferenceType referenceType
    ) {
        return listTransactionsUseCase.list(page, size, fechaInicio, fechaFin,type, source, productName,referenceType);
    }

    @PreAuthorize("hasAuthority('TRANSACTION_LIST_ALL')")
    @GetMapping("/export/pdf")
    public ResponseEntity<byte[]> exportarPDF(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaInicio,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaFin
    ) {
        byte[] pdf = exportTransactionsPDFUseCase.exportar(fechaInicio, fechaFin);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=transacciones.pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdf);
    }

}

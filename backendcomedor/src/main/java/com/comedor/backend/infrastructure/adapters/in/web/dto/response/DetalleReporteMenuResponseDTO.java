package com.comedor.backend.infrastructure.adapters.in.web.dto.response;

import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
public class DetalleReporteMenuResponseDTO {
    private int id;
    private LocalDate date;
    private String day;
    private String menu;
    private List<PersonaResponseDTO> cocineras;

    // 🔥 Mantén solo este, borra el 'registroantiguo'
    private List<StockMovementResponseDTO> registro;

    private List<RegistroBeneficiarioResponseDTO> beneficiarios;
    private ResumenReporteMenuResponseDTO resumenReporteMenu;

    private Integer quantityPrepared;
    private Integer quantityRemaining;
    private String status;
}
package com.comedor.backend.infrastructure.adapters.in.web.dto.response;

import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
public class MenuReportDetailResponseDTO {
    private int id;
    private LocalDate date;
    private String day;
    private String menu;
    private List<PersonResponseDTO> cocineras;

    private List<StockMovementResponseDTO> registro;

    private List<BeneficiaryRecordResponseDTO> beneficiarios;
    private ResumenReporteMenuResponseDTO resumenReporteMenu;

    private Integer quantityPrepared;
    private Integer quantityRemaining;
    private String status;
}
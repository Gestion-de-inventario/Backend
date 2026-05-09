package com.comedor.backend.infrastructure.adapters.in.web.dto.response;

import lombok.Data;

import java.time.LocalDate;

@Data
public class ReporteMenuResponseDTO {
    private int id;
    private LocalDate date;
    private String day;
}

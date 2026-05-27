package com.comedor.backend.infrastructure.adapters.in.web.dto.response;

import com.comedor.backend.domain.model.enums.EstadoReporteMenu;
import lombok.Data;

import java.time.LocalDate;

@Data
public class ReporteMenuResponseDTO {
    private Integer id;
    private LocalDate date;
    private String day;
    private String dishName;
    private Integer quantityPrepared;
    private Integer quantityRemaining;
    private EstadoReporteMenu status;
}

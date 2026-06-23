package com.comedor.backend.infrastructure.adapters.in.web.dto.response;

import com.comedor.backend.domain.model.enums.EstadoReporteMenu;
import com.comedor.backend.infrastructure.adapters.out.persistence.entity.DishMenuEntity;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
public class ReporteMenuResponseDTO {
    private Integer id;
    private LocalDate date;
    private String day;
    private Integer dishId;
    private String dishName;
    private List<StockMovementResponseDTO> registers;
    private List<RegistroBeneficiarioResponseDTO> beneficiaries;
    private List<Integer> cooks;
    private Integer quantityPrepared;
    private Integer quantityRemaining;
    private EstadoReporteMenu status;
}

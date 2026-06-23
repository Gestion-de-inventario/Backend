package com.comedor.backend.infrastructure.adapters.in.web.dto.response;

import com.comedor.backend.domain.model.StockMovement;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
@AllArgsConstructor
public class StockConsumptionResultResponseDTO {

    private List<StockMovement> movimientos;

    private BigDecimal totalSpent;
}
package com.comedor.backend.infrastructure.adapters.in.web.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AlertaStockResponseDTO {
    private int id;
    private String name;
    private String unit;
    private BigDecimal stock;
    private BigDecimal reorderPoint;
    private String categoria;
}
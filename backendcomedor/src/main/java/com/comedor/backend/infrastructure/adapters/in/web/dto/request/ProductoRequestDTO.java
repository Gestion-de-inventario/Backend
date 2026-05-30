package com.comedor.backend.infrastructure.adapters.in.web.dto.request;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class ProductoRequestDTO {
    private String name;
    private Integer  categoryId;
    private Integer  tagId;
    private String unit;
    private BigDecimal stock;
    private BigDecimal reorderPoint;
}

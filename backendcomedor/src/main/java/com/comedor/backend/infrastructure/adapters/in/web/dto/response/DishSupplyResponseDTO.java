package com.comedor.backend.infrastructure.adapters.in.web.dto.response;

import lombok.Data;

import java.math.BigDecimal;
@Data
public class DishSupplyResponseDTO {

    private Integer productId;

    private String productName;

    private BigDecimal quantityNeeded;

    private String unit;
}
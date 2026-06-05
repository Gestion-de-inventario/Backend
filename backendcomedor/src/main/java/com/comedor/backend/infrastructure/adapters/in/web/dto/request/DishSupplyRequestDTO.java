package com.comedor.backend.infrastructure.adapters.in.web.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class DishSupplyRequestDTO {
    @NotNull
    private Integer productId;

    @NotNull
    @Positive
    private BigDecimal quantityNeeded;
}
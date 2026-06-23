package com.comedor.backend.infrastructure.adapters.in.web.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
public class MissingProductResponseDTO {
    private Integer productId;

    private String productName;

    private String productUnit;

    private BigDecimal quantityNeeded;
}

package com.comedor.backend.infrastructure.adapters.in.web.dto.request;

import lombok.Data;

import java.math.BigDecimal;
@Data
public class CreatePurchaseDetailRequestDTO {

    private Integer productId;

    private BigDecimal quantity;

    private BigDecimal unitPrice;

}
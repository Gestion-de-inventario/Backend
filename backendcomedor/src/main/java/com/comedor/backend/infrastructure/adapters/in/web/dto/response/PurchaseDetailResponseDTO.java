package com.comedor.backend.infrastructure.adapters.in.web.dto.response;

import lombok.Data;

import java.math.BigDecimal;
@Data
public class PurchaseDetailResponseDTO {

    private Integer productId;

    private String productName;

    private String productUnit;

    private BigDecimal quantity;

    private BigDecimal unitPrice;

    private BigDecimal subTotal;

}

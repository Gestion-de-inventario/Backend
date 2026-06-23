package com.comedor.backend.infrastructure.adapters.in.web.dto.request;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class CreateDonationDetailRequestDTO {
    private Integer productId;
    private BigDecimal quantity;
}

package com.comedor.backend.infrastructure.adapters.in.web.dto.response;

import lombok.Data;

import java.math.BigDecimal;
@Data
public class DonationDetailResponseDTO {

    private Integer donationId;

    private Integer donationDetailId;

    private Integer productId;

    private String productName;

    private String productUnit;

    private BigDecimal quantity;

}

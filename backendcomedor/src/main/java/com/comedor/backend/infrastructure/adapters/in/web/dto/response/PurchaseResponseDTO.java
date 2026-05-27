package com.comedor.backend.infrastructure.adapters.in.web.dto.response;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
@Data
public class PurchaseResponseDTO {

    private Integer id;

    private LocalDate purchaseDate;

    private String status;

    private BigDecimal totalSpent;

    private List<PurchaseDetailResponseDTO> details;

}
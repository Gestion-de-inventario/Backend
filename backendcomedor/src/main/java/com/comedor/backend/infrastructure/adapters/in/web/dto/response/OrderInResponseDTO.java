package com.comedor.backend.infrastructure.adapters.in.web.dto.response;

import com.comedor.backend.domain.model.enums.ProductSource;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class OrderInResponseDTO {

    private String reference;

    private Integer id;

    private ProductSource source;

    private LocalDate date;

    private String status;

    private BigDecimal totalSpent;

}
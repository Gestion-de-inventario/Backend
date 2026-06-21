package com.comedor.backend.infrastructure.adapters.in.web.dto.response;

import com.comedor.backend.domain.model.enums.FuenteProducto;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class OrderInResponseDTO {

    private String reference;

    private Integer id;

    private FuenteProducto source;

    private LocalDate date;

    private String status;

    private BigDecimal totalSpent;

}
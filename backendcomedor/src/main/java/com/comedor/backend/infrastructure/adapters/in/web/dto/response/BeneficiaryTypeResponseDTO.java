package com.comedor.backend.infrastructure.adapters.in.web.dto.response;

import com.comedor.backend.domain.model.enums.Status;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class BeneficiaryTypeResponseDTO {
    private Integer id;

    private String name;

    private String desc;

    private BigDecimal menu_cost;

    private Status status;
}

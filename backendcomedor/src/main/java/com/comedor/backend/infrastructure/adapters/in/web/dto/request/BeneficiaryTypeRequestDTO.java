package com.comedor.backend.infrastructure.adapters.in.web.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class BeneficiaryTypeRequestDTO {

    @NotBlank(message = "Los nombres son obligatorios")
    private String name;

    private String desc;

    private BigDecimal menu_cost;
}

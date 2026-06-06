package com.comedor.backend.infrastructure.adapters.in.web.dto.response;

import com.comedor.backend.domain.model.enums.Estado;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class BeneficiarioResponseDTO {
    private int id;
    private String dni;
    private String name;
    private String lastname;
    private BigDecimal menu_cost;
    private Estado status;
}
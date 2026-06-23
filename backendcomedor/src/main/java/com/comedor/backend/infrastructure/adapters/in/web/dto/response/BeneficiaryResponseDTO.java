package com.comedor.backend.infrastructure.adapters.in.web.dto.response;

import com.comedor.backend.domain.model.enums.Status;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class BeneficiaryResponseDTO {
    private int id;
    private String dni;
    private String name;
    private String lastname;
    private String beneficiaryType;
    private Integer beneficiaryTypeId;
    private BigDecimal menu_cost;
    private Status status;
}
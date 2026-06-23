package com.comedor.backend.infrastructure.adapters.in.web.dto.request;

import lombok.Data;

@Data
public class EditBeneficiaryRequestDTO {
    private String dni;
    private String name;
    private String lastname;
    private Integer beneficiaryTypeId;
}

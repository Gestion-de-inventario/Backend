package com.comedor.backend.infrastructure.adapters.in.web.dto.response;

import com.comedor.backend.domain.model.enums.Estado;
import lombok.Data;

@Data
public class BeneficiarioResponseDTO {
    private int id;
    private String dni;
    private String name;
    private String lastname;
    private Estado status;
}
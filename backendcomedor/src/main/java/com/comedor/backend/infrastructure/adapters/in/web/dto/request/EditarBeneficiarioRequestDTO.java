package com.comedor.backend.infrastructure.adapters.in.web.dto.request;

import com.comedor.backend.domain.model.enums.Estado;
import lombok.Data;

@Data
public class EditarBeneficiarioRequestDTO {
    private String dni;
    private String name;
    private String lastName;
    private Estado status;
}

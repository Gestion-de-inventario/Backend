package com.comedor.backend.infrastructure.adapters.in.web.dto.response;

import com.comedor.backend.domain.model.enums.ModuleCode;
import com.comedor.backend.domain.model.enums.PermissionCode;
import lombok.Data;

@Data
public class PermissionResponseDTO {
    private String description;
    private PermissionCode code;
    private ModuleCode module;
}

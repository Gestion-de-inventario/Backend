package com.comedor.backend.infrastructure.adapters.in.web.dto.request;

import com.comedor.backend.domain.model.enums.PermissionCode;
import lombok.Data;

import java.util.Set;

@Data
public class PermissionsAsigmentRequestDTO {
    private Set<PermissionCode> permissions;
}

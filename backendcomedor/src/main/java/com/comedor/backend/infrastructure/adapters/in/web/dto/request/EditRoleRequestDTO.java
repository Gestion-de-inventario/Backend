package com.comedor.backend.infrastructure.adapters.in.web.dto.request;

import com.comedor.backend.domain.model.enums.Estado;
import com.comedor.backend.domain.model.enums.PermissionCode;
import lombok.Data;

import java.util.Set;
@Data
public class EditRoleRequestDTO {
    private String name;
    private Estado status;
    private Set<PermissionCode> permissions;
}

package com.comedor.backend.infrastructure.adapters.in.web.dto.request;

import com.comedor.backend.domain.model.enums.AuditAction;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AuditRequestDTO {
    private String entityType;
    private int entityId;
    private String entityName;
    private AuditAction action;
    private Map<String, Object> details;


    public String getEntityName() {
        return entityName.toUpperCase();
    }
}

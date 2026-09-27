package com.comedor.backend.infrastructure.adapters.in.web.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.comedor.backend.domain.model.enums.AuditAction;
import java.time.LocalDateTime;
import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AuditResponseDTO {
    private int id;
    private String username;

    private String entityType;
    private int entityId;
    private String entityName;

    private AuditAction action;

    private Map<String, Object> details;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime dateTime;
}

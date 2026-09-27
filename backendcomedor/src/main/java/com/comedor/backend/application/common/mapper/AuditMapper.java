package com.comedor.backend.application.common.mapper;

import com.comedor.backend.domain.model.Audit;
import com.comedor.backend.domain.model.User;
import com.comedor.backend.infrastructure.adapters.in.web.dto.request.AuditRequestDTO;
import com.comedor.backend.infrastructure.adapters.in.web.dto.response.AuditResponseDTO;
import org.springframework.stereotype.Component;

@Component
public class AuditMapper {

    public AuditResponseDTO toResponseDTO(Audit audit) {

        AuditResponseDTO dto = new AuditResponseDTO();

        dto.setId(audit.getId());

        dto.setUsername(
                audit.getUser().getPersona().getName().toUpperCase() + " " +
                        audit.getUser().getPersona().getLastname().toUpperCase()
        );

        dto.setEntityType(audit.getEntityType());
        dto.setEntityId(audit.getEntityId());
        dto.setEntityName(audit.getEntityName());
        dto.setAction(audit.getAction());
        dto.setDetails(audit.getDetails());
        dto.setDateTime(audit.getDateTime());

        return dto;
    }

    public Audit toDomain(
            AuditRequestDTO request) {

        Audit audit = new Audit();
        User user = new User();
        audit.setUser(user);
        audit.setEntityType(request.getEntityType());
        audit.setEntityId(request.getEntityId());
        audit.setEntityName(request.getEntityName());
        audit.setAction(request.getAction());
        audit.setDetails(request.getDetails());

        return audit;
    }
}

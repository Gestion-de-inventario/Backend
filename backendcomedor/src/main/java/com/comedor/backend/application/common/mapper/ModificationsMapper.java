package com.comedor.backend.application.common.mapper;

import com.comedor.backend.domain.model.Modifications;
import com.comedor.backend.infrastructure.adapters.in.web.dto.response.ModificationsResponseDTO;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ModificationsMapper {
    public ModificationsResponseDTO toResponseDTO(Modifications modifications) {
        ModificationsResponseDTO dto = new ModificationsResponseDTO();
        dto.setId(modifications.getId());
        dto.setUsername(modifications.getUser().getPersona().getName().toUpperCase() + " " +
                modifications.getUser().getPersona().getLastname().toUpperCase());
        dto.setEditedClass(modifications.getEditedClass());
        dto.setName(modifications.getName());
        dto.setEditedAttribute(modifications.getEditedAttribute());
        dto.setPreviousValue(modifications.getPreviousValue());
        dto.setNewValue(modifications.getNewValue());
        dto.setDateTime(modifications.getDateTime());
        return dto;
    }
}

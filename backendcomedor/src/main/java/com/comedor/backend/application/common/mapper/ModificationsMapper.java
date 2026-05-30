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
        dto.setEditedAttribute(modifications.getEditedAttribute());
        dto.setPreviousValue(modifications.getPreviousValue());
        dto.setNewValue(modifications.getNewValue());
        dto.setDateTime(modifications.getDateTime());
        return dto;
    }

    public List<ModificationsResponseDTO> toResponseDTOList(List<Modifications> modifications) {
        return modifications.stream()
                .map(this::toResponseDTO)
                .collect(java.util.stream.Collectors.toList());
    }
}

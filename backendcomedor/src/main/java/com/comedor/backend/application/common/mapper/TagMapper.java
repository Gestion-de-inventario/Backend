package com.comedor.backend.application.common.mapper;

import com.comedor.backend.domain.model.Tag;
import com.comedor.backend.infrastructure.adapters.in.web.dto.request.EtiquetaRequestDTO;
import com.comedor.backend.infrastructure.adapters.in.web.dto.response.EtiquetaResponseDTO;
import org.springframework.stereotype.Component;

import java.util.List;
@Component
public class TagMapper {

    public Tag toDomain(EtiquetaRequestDTO etiquetaRequestDTO)
    {
        if (etiquetaRequestDTO == null)
            return null;
        Tag tag = new Tag();
        tag.setName(etiquetaRequestDTO.getName());
        return tag;
    }

    public EtiquetaResponseDTO toEtiquetaResponseDTO (Tag tag){
        if (tag == null)return null;
        EtiquetaResponseDTO etiquetaResponseDTO = new EtiquetaResponseDTO();
        etiquetaResponseDTO.setName(tag.getName());
        etiquetaResponseDTO.setId(tag.getId());
        etiquetaResponseDTO.setStatus(tag.getStatus());
        return etiquetaResponseDTO;
    }

    public List<EtiquetaResponseDTO> toListEtiquetaResponseDTO(List<Tag> tags){
        if (tags == null)return null;
        return tags.stream().map(this::toEtiquetaResponseDTO).toList();
    }
}

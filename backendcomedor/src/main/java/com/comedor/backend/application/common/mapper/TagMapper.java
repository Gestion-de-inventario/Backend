package com.comedor.backend.application.common.mapper;

import com.comedor.backend.domain.model.Tag;
import com.comedor.backend.infrastructure.adapters.in.web.dto.request.TagRequestDTO;
import com.comedor.backend.infrastructure.adapters.in.web.dto.response.TagResponseDTO;
import org.springframework.stereotype.Component;

import java.util.List;
@Component
public class TagMapper {

    public Tag toDomain(TagRequestDTO tagRequestDTO)
    {
        if (tagRequestDTO == null)
            return null;
        Tag tag = new Tag();
        tag.setName(tagRequestDTO.getName());
        return tag;
    }

    public TagResponseDTO toEtiquetaResponseDTO (Tag tag){
        if (tag == null)return null;
        TagResponseDTO tagResponseDTO = new TagResponseDTO();
        tagResponseDTO.setName(tag.getName());
        tagResponseDTO.setId(tag.getId());
        tagResponseDTO.setStatus(tag.getStatus());
        return tagResponseDTO;
    }

    public List<TagResponseDTO> toListEtiquetaResponseDTO(List<Tag> tags){
        if (tags == null)return null;
        return tags.stream().map(this::toEtiquetaResponseDTO).toList();
    }
}

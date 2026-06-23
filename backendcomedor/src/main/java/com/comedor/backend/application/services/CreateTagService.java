package com.comedor.backend.application.services;

import com.comedor.backend.application.common.mapper.TagMapper;
import com.comedor.backend.application.ports.in.CreateTagUseCase;
import com.comedor.backend.application.ports.out.TagRepositoryPort;
import com.comedor.backend.domain.exceptions.ExistingTagException;
import com.comedor.backend.domain.model.Tag;
import com.comedor.backend.infrastructure.adapters.in.web.dto.request.TagRequestDTO;
import com.comedor.backend.infrastructure.adapters.in.web.dto.response.TagResponseDTO;

public class CreateTagService implements CreateTagUseCase {
    private final TagRepositoryPort tagRepositoryPort;
    private final TagMapper tagMapper;

    public CreateTagService(TagRepositoryPort tagRepositoryPort, TagMapper tagMapper) {
        this.tagRepositoryPort = tagRepositoryPort;
        this.tagMapper = tagMapper;
    }

    @Override
    public TagResponseDTO crearEtiqueta(TagRequestDTO tagRequestDTO) {
        if(tagRepositoryPort.existByName(tagRequestDTO.getName().toUpperCase()))
        {
            throw new ExistingTagException("Ya existe la etiqueta "+ tagRequestDTO.getName());
        }
       ;
        Tag tag = tagMapper.toDomain(tagRequestDTO);

        return tagMapper.toEtiquetaResponseDTO(tagRepositoryPort.createEtiqueta(tag));
    }
}

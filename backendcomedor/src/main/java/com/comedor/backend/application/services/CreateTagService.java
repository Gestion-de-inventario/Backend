package com.comedor.backend.application.services;

import com.comedor.backend.application.common.mapper.TagMapper;
import com.comedor.backend.application.ports.in.CreateTagUseCase;
import com.comedor.backend.application.ports.in.RegisterAuditUseCase;
import com.comedor.backend.application.ports.out.TagRepositoryPort;
import com.comedor.backend.domain.exceptions.ExistingTagException;
import com.comedor.backend.domain.model.Tag;
import com.comedor.backend.domain.model.enums.AuditAction;
import com.comedor.backend.infrastructure.adapters.in.web.dto.request.AuditRequestDTO;
import com.comedor.backend.infrastructure.adapters.in.web.dto.request.TagRequestDTO;
import com.comedor.backend.infrastructure.adapters.in.web.dto.response.TagResponseDTO;

import java.util.Map;

public class CreateTagService implements CreateTagUseCase {
    private final TagRepositoryPort tagRepositoryPort;
    private final TagMapper tagMapper;
    private final RegisterAuditUseCase registerAuditUseCase;

    public CreateTagService(TagRepositoryPort tagRepositoryPort, TagMapper tagMapper, RegisterAuditUseCase registerAuditUseCase) {
        this.tagRepositoryPort = tagRepositoryPort;
        this.tagMapper = tagMapper;
        this.registerAuditUseCase = registerAuditUseCase;
    }

    @Override
    public TagResponseDTO crearEtiqueta(TagRequestDTO tagRequestDTO) {
        if(tagRepositoryPort.existByName(tagRequestDTO.getName().toUpperCase()))
        {
            throw new ExistingTagException("Ya existe la etiqueta "+ tagRequestDTO.getName());
        }
       ;
        Tag tag = tagMapper.toDomain(tagRequestDTO);

        registerAuditUseCase.registrar(
                new AuditRequestDTO(
                        "Etiqueta",
                        tag.getId(),
                        tag.getName(),
                        AuditAction.CREACION,
                        Map.of(
                                "Nombre", tag.getName()
                        )
                )
        );

        return tagMapper.toEtiquetaResponseDTO(tagRepositoryPort.createEtiqueta(tag));
    }
}

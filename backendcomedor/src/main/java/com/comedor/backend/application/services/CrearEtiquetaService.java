package com.comedor.backend.application.services;

import com.comedor.backend.application.common.mapper.TagMapper;
import com.comedor.backend.application.ports.in.CrearEtiquetaUseCase;
import com.comedor.backend.application.ports.out.TagRepositoryPort;
import com.comedor.backend.domain.exceptions.EtiquetaExistenteException;
import com.comedor.backend.domain.model.Tag;
import com.comedor.backend.infrastructure.adapters.in.web.dto.request.EtiquetaRequestDTO;
import com.comedor.backend.infrastructure.adapters.in.web.dto.response.EtiquetaResponseDTO;

public class CrearEtiquetaService implements CrearEtiquetaUseCase {
    private final TagRepositoryPort tagRepositoryPort;
    private final TagMapper tagMapper;

    public CrearEtiquetaService(TagRepositoryPort tagRepositoryPort, TagMapper tagMapper) {
        this.tagRepositoryPort = tagRepositoryPort;
        this.tagMapper = tagMapper;
    }

    @Override
    public EtiquetaResponseDTO crearEtiqueta(EtiquetaRequestDTO etiquetaRequestDTO) {
        if(tagRepositoryPort.existByName(etiquetaRequestDTO.getName().toUpperCase()))
        {
            throw new EtiquetaExistenteException("Ya existe la etiqueta "+etiquetaRequestDTO.getName());
        }
       ;
        Tag tag = tagMapper.toDomain(etiquetaRequestDTO);

        return tagMapper.toEtiquetaResponseDTO(tagRepositoryPort.createEtiqueta(tag));
    }
}

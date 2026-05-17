package com.comedor.backend.application.services;

import com.comedor.backend.application.common.mapper.TagMapper;
import com.comedor.backend.application.ports.in.ActivarEtiquetaUseCase;
import com.comedor.backend.application.ports.out.TagRepositoryPort;
import com.comedor.backend.infrastructure.adapters.in.web.dto.response.EtiquetaResponseDTO;

public class ActivarEtiquetaService implements ActivarEtiquetaUseCase {
    private final TagRepositoryPort tagRepositoryPort;
    private final TagMapper tagMapper;

    public ActivarEtiquetaService(TagRepositoryPort tagRepositoryPort, TagMapper tagMapper) {
        this.tagRepositoryPort = tagRepositoryPort;
        this.tagMapper = tagMapper;
    }


    @Override
    public EtiquetaResponseDTO activarEtiquetaPorId(int id) {
        return tagMapper.toEtiquetaResponseDTO(tagRepositoryPort.activateById(id));
    }
}

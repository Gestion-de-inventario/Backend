package com.comedor.backend.application.services;

import com.comedor.backend.application.common.mapper.TagMapper;
import com.comedor.backend.application.ports.in.ListTagsByStatusUseCase;
import com.comedor.backend.application.ports.out.TagRepositoryPort;
import com.comedor.backend.domain.model.enums.Status;
import com.comedor.backend.infrastructure.adapters.in.web.dto.response.TagResponseDTO;

import java.util.List;


public class ListTagsByStatusService implements ListTagsByStatusUseCase {

    private final TagRepositoryPort tagRepositoryPort;
    private final TagMapper tagMapper;

    public ListTagsByStatusService(TagRepositoryPort tagRepositoryPort, TagMapper tagMapper) {
        this.tagRepositoryPort = tagRepositoryPort;
        this.tagMapper = tagMapper;
    }

    @Override
    public List<TagResponseDTO> listarEtiquetas(Status status) {
        return tagMapper.toListEtiquetaResponseDTO(tagRepositoryPort.getTags(status));
    }
}

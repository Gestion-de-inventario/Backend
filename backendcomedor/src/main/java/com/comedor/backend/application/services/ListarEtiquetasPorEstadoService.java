package com.comedor.backend.application.services;

import com.comedor.backend.application.common.mapper.TagMapper;
import com.comedor.backend.application.ports.in.ListarEtiquetasPorEstadoUseCase;
import com.comedor.backend.application.ports.out.TagRepositoryPort;
import com.comedor.backend.domain.model.enums.Estado;
import com.comedor.backend.infrastructure.adapters.in.web.dto.response.EtiquetaResponseDTO;

import java.util.List;


public class ListarEtiquetasPorEstadoService implements ListarEtiquetasPorEstadoUseCase {

    private final TagRepositoryPort tagRepositoryPort;
    private final TagMapper tagMapper;

    public ListarEtiquetasPorEstadoService(TagRepositoryPort tagRepositoryPort, TagMapper tagMapper) {
        this.tagRepositoryPort = tagRepositoryPort;
        this.tagMapper = tagMapper;
    }

    @Override
    public List<EtiquetaResponseDTO> listarEtiquetas(Estado estado) {
        return tagMapper.toListEtiquetaResponseDTO(tagRepositoryPort.getTags(estado));
    }
}

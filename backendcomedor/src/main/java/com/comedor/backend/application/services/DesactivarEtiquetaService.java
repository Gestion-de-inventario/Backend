package com.comedor.backend.application.services;

import com.comedor.backend.application.common.mapper.TagMapper;
import com.comedor.backend.application.ports.in.DesactivarEtiquetaUseCase;
import com.comedor.backend.application.ports.out.TagRepositoryPort;
import com.comedor.backend.infrastructure.adapters.in.web.dto.response.EtiquetaResponseDTO;

public class DesactivarEtiquetaService implements DesactivarEtiquetaUseCase {
    private final TagRepositoryPort etiquetaRepository;
    private final TagMapper tagMapper;

    public DesactivarEtiquetaService(TagRepositoryPort etiquetaRepository, TagMapper tagMapper) {
        this.etiquetaRepository = etiquetaRepository;
        this.tagMapper = tagMapper;
    }

    @Override
    public EtiquetaResponseDTO desactivarEtiquetaPorId(int id) {
        return tagMapper.toEtiquetaResponseDTO(etiquetaRepository.deactivateById(id));
    }
}

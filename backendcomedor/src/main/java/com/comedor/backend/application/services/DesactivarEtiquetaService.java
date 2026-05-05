package com.comedor.backend.application.services;

import com.comedor.backend.application.common.mapper.EtiquetaMapper;
import com.comedor.backend.application.ports.in.DesactivarEtiquetaUseCase;
import com.comedor.backend.application.ports.out.EtiquetaRepositoryPort;
import com.comedor.backend.domain.model.Etiqueta;
import com.comedor.backend.infrastructure.adapters.in.web.dto.response.EtiquetaResponseDTO;

public class DesactivarEtiquetaService implements DesactivarEtiquetaUseCase {
    private final EtiquetaRepositoryPort etiquetaRepository;
    private final EtiquetaMapper etiquetaMapper;

    public DesactivarEtiquetaService(EtiquetaRepositoryPort etiquetaRepository, EtiquetaMapper etiquetaMapper) {
        this.etiquetaRepository = etiquetaRepository;
        this.etiquetaMapper = etiquetaMapper;
    }

    @Override
    public EtiquetaResponseDTO desactivarEtiquetaPorId(int id) {
        return etiquetaMapper.toEtiquetaResponseDTO(etiquetaRepository.deactivateById(id));
    }
}

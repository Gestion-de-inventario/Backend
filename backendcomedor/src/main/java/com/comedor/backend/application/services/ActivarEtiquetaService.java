package com.comedor.backend.application.services;

import com.comedor.backend.application.common.mapper.EtiquetaMapper;
import com.comedor.backend.application.ports.in.ActivarEtiquetaUseCase;
import com.comedor.backend.application.ports.out.EtiquetaRepositoryPort;
import com.comedor.backend.infrastructure.adapters.in.web.dto.response.EtiquetaResponseDTO;

public class ActivarEtiquetaService implements ActivarEtiquetaUseCase {
    private final EtiquetaRepositoryPort etiquetaRepositoryPort;
    private final EtiquetaMapper etiquetaMapper;

    public ActivarEtiquetaService(EtiquetaRepositoryPort etiquetaRepositoryPort, EtiquetaMapper etiquetaMapper) {
        this.etiquetaRepositoryPort = etiquetaRepositoryPort;
        this.etiquetaMapper = etiquetaMapper;
    }


    @Override
    public EtiquetaResponseDTO activarEtiquetaPorId(int id) {
        return etiquetaMapper.toEtiquetaResponseDTO(etiquetaRepositoryPort.activateById(id));
    }
}

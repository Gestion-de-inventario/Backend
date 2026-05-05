package com.comedor.backend.application.services;

import com.comedor.backend.application.common.mapper.EtiquetaMapper;
import com.comedor.backend.application.ports.in.ListarEtiquetasPorEstadoUseCase;
import com.comedor.backend.application.ports.out.EtiquetaRepositoryPort;
import com.comedor.backend.domain.model.enums.Estado;
import com.comedor.backend.infrastructure.adapters.in.web.dto.response.EtiquetaResponseDTO;

import java.util.List;


public class ListarEtiquetasPorEstadoService implements ListarEtiquetasPorEstadoUseCase {

    private final EtiquetaRepositoryPort etiquetaRepositoryPort;
    private final EtiquetaMapper etiquetaMapper;

    public ListarEtiquetasPorEstadoService(EtiquetaRepositoryPort etiquetaRepositoryPort, EtiquetaMapper etiquetaMapper) {
        this.etiquetaRepositoryPort = etiquetaRepositoryPort;
        this.etiquetaMapper = etiquetaMapper;
    }

    @Override
    public List<EtiquetaResponseDTO> listarEtiquetas(Estado estado) {
        return etiquetaMapper.toListEtiquetaResponseDTO(etiquetaRepositoryPort.getEtiquetas(estado));
    }
}

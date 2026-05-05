package com.comedor.backend.application.services;

import com.comedor.backend.application.common.mapper.EtiquetaMapper;
import com.comedor.backend.application.ports.in.CrearEtiquetaUseCase;
import com.comedor.backend.application.ports.out.EtiquetaRepositoryPort;
import com.comedor.backend.domain.exceptions.EtiquetaExistenteException;
import com.comedor.backend.domain.model.Etiqueta;
import com.comedor.backend.infrastructure.adapters.in.web.dto.request.EtiquetaRequestDTO;
import com.comedor.backend.infrastructure.adapters.in.web.dto.response.EtiquetaResponseDTO;

public class CrearEtiquetaService implements CrearEtiquetaUseCase {
    private final EtiquetaRepositoryPort etiquetaRepositoryPort;
    private final EtiquetaMapper etiquetaMapper;

    public CrearEtiquetaService(EtiquetaRepositoryPort etiquetaRepositoryPort, EtiquetaMapper etiquetaMapper) {
        this.etiquetaRepositoryPort = etiquetaRepositoryPort;
        this.etiquetaMapper = etiquetaMapper;
    }

    @Override
    public EtiquetaResponseDTO crearEtiqueta(EtiquetaRequestDTO etiquetaRequestDTO) {
        if(etiquetaRepositoryPort.existByName(etiquetaRequestDTO.getName()))
        {
            throw new EtiquetaExistenteException("Ya existe la etiqueta "+etiquetaRequestDTO.getName());
        }
       ;
        Etiqueta etiqueta =etiquetaMapper.toDomain(etiquetaRequestDTO);

        return etiquetaMapper.toEtiquetaResponseDTO(etiquetaRepositoryPort.createEtiqueta(etiqueta));
    }
}

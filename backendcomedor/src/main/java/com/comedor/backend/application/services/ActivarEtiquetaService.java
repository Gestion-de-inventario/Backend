package com.comedor.backend.application.services;

import com.comedor.backend.application.common.mapper.TagMapper;
import com.comedor.backend.application.ports.in.ActivarEtiquetaUseCase;
import com.comedor.backend.application.ports.in.RegistrarModificacionUseCase;
import com.comedor.backend.application.ports.out.TagRepositoryPort;
import com.comedor.backend.infrastructure.adapters.in.web.dto.request.ModificationsRequestDTO;
import com.comedor.backend.infrastructure.adapters.in.web.dto.response.EtiquetaResponseDTO;

public class ActivarEtiquetaService implements ActivarEtiquetaUseCase {
    private final TagRepositoryPort tagRepositoryPort;
    private final TagMapper tagMapper;
    private final RegistrarModificacionUseCase registrarModificacionUseCase;

    public ActivarEtiquetaService(TagRepositoryPort tagRepositoryPort, TagMapper tagMapper, RegistrarModificacionUseCase registrarModificacionUseCase) {
        this.tagRepositoryPort = tagRepositoryPort;
        this.tagMapper = tagMapper;
        this.registrarModificacionUseCase = registrarModificacionUseCase;
    }


    @Override
    public EtiquetaResponseDTO activarEtiquetaPorId(int id) {
        EtiquetaResponseDTO resultado = tagMapper.toEtiquetaResponseDTO(tagRepositoryPort.activateById(id));

        registrarModificacionUseCase.registrar(new ModificationsRequestDTO(
                "Etiqueta",
                "status",
                "INACTIVO",
                "ACTIVO"
        ));

        return resultado;
    }
}

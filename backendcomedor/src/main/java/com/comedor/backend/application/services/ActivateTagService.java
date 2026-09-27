package com.comedor.backend.application.services;

import com.comedor.backend.application.common.mapper.TagMapper;
import com.comedor.backend.application.ports.in.ActivateTagUseCase;

import com.comedor.backend.application.ports.in.RegisterAuditUseCase;
import com.comedor.backend.application.ports.out.TagRepositoryPort;

import com.comedor.backend.domain.model.enums.AuditAction;
import com.comedor.backend.infrastructure.adapters.in.web.dto.request.AuditRequestDTO;
import com.comedor.backend.infrastructure.adapters.in.web.dto.response.TagResponseDTO;

import java.util.Map;

public class ActivateTagService implements ActivateTagUseCase {
    private final TagRepositoryPort tagRepositoryPort;
    private final TagMapper tagMapper;
    private final RegisterAuditUseCase registerAuditUseCase;

    public ActivateTagService(TagRepositoryPort tagRepositoryPort, TagMapper tagMapper,RegisterAuditUseCase registerAuditUseCase) {
        this.tagRepositoryPort = tagRepositoryPort;
        this.tagMapper = tagMapper;
        this.registerAuditUseCase = registerAuditUseCase;
    }


    @Override
    public TagResponseDTO activarEtiquetaPorId(int id) {
        TagResponseDTO resultado = tagMapper.toEtiquetaResponseDTO(tagRepositoryPort.activateById(id));

        registerAuditUseCase.registrar(
                new AuditRequestDTO(
                        "Etiqueta",
                        id,
                        resultado.getName(),
                        AuditAction.MODIFICACION,
                        Map.of(
                                "attribute", "estado",
                                "previousValue", "INACTIVO",
                                "newValue", "ACTIVO"
                        )
                )
        );

        return resultado;
    }
}

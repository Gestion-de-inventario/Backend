package com.comedor.backend.application.services;

import com.comedor.backend.application.common.mapper.TagMapper;
import com.comedor.backend.application.ports.in.DeactivateTagUseCase;
import com.comedor.backend.application.ports.in.RegisterAuditUseCase;
import com.comedor.backend.application.ports.out.TagRepositoryPort;
import com.comedor.backend.domain.model.enums.AuditAction;
import com.comedor.backend.infrastructure.adapters.in.web.dto.request.AuditRequestDTO;
import com.comedor.backend.infrastructure.adapters.in.web.dto.response.TagResponseDTO;

import java.util.Map;

public class DeactivateTagService implements DeactivateTagUseCase {
    private final TagRepositoryPort etiquetaRepository;
    private final TagMapper tagMapper;
    private final RegisterAuditUseCase registerAuditUseCase;

    public DeactivateTagService(TagRepositoryPort etiquetaRepository, TagMapper tagMapper,RegisterAuditUseCase registerAuditUseCase) {
        this.etiquetaRepository = etiquetaRepository;
        this.tagMapper = tagMapper;
        this.registerAuditUseCase = registerAuditUseCase;
    }

    @Override
    public TagResponseDTO desactivarEtiquetaPorId(int id) {
        TagResponseDTO resultado = tagMapper.toEtiquetaResponseDTO(etiquetaRepository.deactivateById(id));
        registerAuditUseCase.registrar(
                new AuditRequestDTO(
                        "Etiqueta",
                        id,
                        resultado.getName(),
                        AuditAction.MODIFICACION,
                        Map.of(
                                "attribute", "estado",
                                "previousValue", "ACTIVO",
                                "newValue", "INACTIVO"
                        )
                )
        );

        return resultado;
    }
}

package com.comedor.backend.application.services;

import com.comedor.backend.application.common.mapper.TagMapper;
import com.comedor.backend.application.ports.in.ActivateTagUseCase;
import com.comedor.backend.application.ports.in.RegisterModificationUseCase;
import com.comedor.backend.application.ports.out.TagRepositoryPort;
import com.comedor.backend.infrastructure.adapters.in.web.dto.request.ModificationsRequestDTO;
import com.comedor.backend.infrastructure.adapters.in.web.dto.response.TagResponseDTO;

public class ActivateTagService implements ActivateTagUseCase {
    private final TagRepositoryPort tagRepositoryPort;
    private final TagMapper tagMapper;
    private final RegisterModificationUseCase registerModificationUseCase;

    public ActivateTagService(TagRepositoryPort tagRepositoryPort, TagMapper tagMapper, RegisterModificationUseCase registerModificationUseCase) {
        this.tagRepositoryPort = tagRepositoryPort;
        this.tagMapper = tagMapper;
        this.registerModificationUseCase = registerModificationUseCase;
    }


    @Override
    public TagResponseDTO activarEtiquetaPorId(int id) {
        TagResponseDTO resultado = tagMapper.toEtiquetaResponseDTO(tagRepositoryPort.activateById(id));

        registerModificationUseCase.registrar(new ModificationsRequestDTO(
                "Etiqueta",
                "status",
                "INACTIVO",
                "ACTIVO"
        ));

        return resultado;
    }
}

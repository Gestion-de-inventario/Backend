package com.comedor.backend.application.services;

import com.comedor.backend.application.common.mapper.TagMapper;
import com.comedor.backend.application.ports.in.DeactivateTagUseCase;
import com.comedor.backend.application.ports.in.RegisterModificationUseCase;
import com.comedor.backend.application.ports.out.TagRepositoryPort;
import com.comedor.backend.infrastructure.adapters.in.web.dto.request.ModificationsRequestDTO;
import com.comedor.backend.infrastructure.adapters.in.web.dto.response.TagResponseDTO;

public class DeactivateTagService implements DeactivateTagUseCase {
    private final TagRepositoryPort etiquetaRepository;
    private final TagMapper tagMapper;
    private final RegisterModificationUseCase registerModificationUseCase;

    public DeactivateTagService(TagRepositoryPort etiquetaRepository, TagMapper tagMapper, RegisterModificationUseCase registerModificationUseCase) {
        this.etiquetaRepository = etiquetaRepository;
        this.tagMapper = tagMapper;
        this.registerModificationUseCase = registerModificationUseCase;
    }

    @Override
    public TagResponseDTO desactivarEtiquetaPorId(int id) {
        TagResponseDTO resultado = tagMapper.toEtiquetaResponseDTO(etiquetaRepository.deactivateById(id));
        registerModificationUseCase.registrar(new ModificationsRequestDTO(
                "Etiqueta",
                "status",
                "ACTIVO",
                "INACTIVO"
        ));
        return resultado;
    }
}

package com.comedor.backend.application.services;

import com.comedor.backend.application.common.mapper.TagMapper;
import com.comedor.backend.application.ports.in.DesactivarEtiquetaUseCase;
import com.comedor.backend.application.ports.in.RegistrarModificacionUseCase;
import com.comedor.backend.application.ports.out.TagRepositoryPort;
import com.comedor.backend.infrastructure.adapters.in.web.dto.request.ModificationsRequestDTO;
import com.comedor.backend.infrastructure.adapters.in.web.dto.response.EtiquetaResponseDTO;

public class DesactivarEtiquetaService implements DesactivarEtiquetaUseCase {
    private final TagRepositoryPort etiquetaRepository;
    private final TagMapper tagMapper;
    private final RegistrarModificacionUseCase registrarModificacionUseCase;

    public DesactivarEtiquetaService(TagRepositoryPort etiquetaRepository, TagMapper tagMapper, RegistrarModificacionUseCase registrarModificacionUseCase) {
        this.etiquetaRepository = etiquetaRepository;
        this.tagMapper = tagMapper;
        this.registrarModificacionUseCase = registrarModificacionUseCase;
    }

    @Override
    public EtiquetaResponseDTO desactivarEtiquetaPorId(int id) {
        EtiquetaResponseDTO resultado = tagMapper.toEtiquetaResponseDTO(etiquetaRepository.deactivateById(id));
        registrarModificacionUseCase.registrar(new ModificationsRequestDTO(
                "Etiqueta",
                "status",
                "ACTIVO",
                "INACTIVO"
        ));
        return resultado;
    }
}

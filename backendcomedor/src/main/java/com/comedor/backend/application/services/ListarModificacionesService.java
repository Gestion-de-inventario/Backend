package com.comedor.backend.application.services;

import com.comedor.backend.application.common.mapper.ModificationsMapper;
import com.comedor.backend.application.ports.in.ListarModificacionesUseCase;
import com.comedor.backend.application.ports.out.ModificationsRepositoryPort;
import com.comedor.backend.infrastructure.adapters.in.web.dto.response.ModificationsResponseDTO;

import java.util.List;

public class ListarModificacionesService implements ListarModificacionesUseCase {

    private final ModificationsRepositoryPort modificationsRepositoryPort;
    private final ModificationsMapper modificationsMapper;

    public ListarModificacionesService(ModificationsRepositoryPort modificationsRepositoryPort, ModificationsMapper modificationsMapper) {
        this.modificationsRepositoryPort = modificationsRepositoryPort;
        this.modificationsMapper = modificationsMapper;
    }

    @Override
    public List<ModificationsResponseDTO> listar() {
        return modificationsMapper.toResponseDTOList(
                modificationsRepositoryPort.listar()
        );
    }
}

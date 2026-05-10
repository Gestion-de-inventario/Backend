package com.comedor.backend.application.services;

import com.comedor.backend.application.common.mapper.TransaccionMapper;
import com.comedor.backend.application.ports.in.ListarTransaccionesUseCase;
import com.comedor.backend.application.ports.out.TransaccionRepositoryPort;
import com.comedor.backend.infrastructure.adapters.in.web.dto.response.TransaccionResponseDTO;

import java.util.List;

public class ListarTransaccionesService implements ListarTransaccionesUseCase {
    private final TransaccionRepositoryPort repository;
    private final TransaccionMapper mapper;

    public ListarTransaccionesService(TransaccionRepositoryPort repository, TransaccionMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }


    @Override
    public List<TransaccionResponseDTO> listarTransaccionesUseCase() {
        return mapper.toListDto(repository.showTransacciones());
    }
}

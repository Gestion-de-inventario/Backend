package com.comedor.backend.application.services;

import com.comedor.backend.application.common.mapper.TransactionMapper;
import com.comedor.backend.application.ports.in.ListarTransaccionesUseCase;
import com.comedor.backend.application.ports.out.TransactionRepositoryPort;
import com.comedor.backend.infrastructure.adapters.in.web.dto.response.TransaccionResponseDTO;

import java.util.List;

public class ListarTransaccionesService implements ListarTransaccionesUseCase {
    private final TransactionRepositoryPort repository;
    private final TransactionMapper mapper;

    public ListarTransaccionesService(TransactionRepositoryPort repository, TransactionMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }


    @Override
    public List<TransaccionResponseDTO> listarTransaccionesUseCase() {
        return mapper.toListDto(repository.showTransacciones());
    }
}

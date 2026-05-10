package com.comedor.backend.application.services;

import com.comedor.backend.application.common.mapper.TransaccionMapper;
import com.comedor.backend.application.ports.in.RegistrarTransaccionUseCase;
import com.comedor.backend.application.ports.out.TransaccionRepositoryPort;
import com.comedor.backend.domain.model.Transacciones;
import com.comedor.backend.infrastructure.adapters.in.web.dto.request.TransaccionRequestDTO;
import com.comedor.backend.infrastructure.adapters.in.web.dto.response.TransaccionResponseDTO;

import java.time.LocalDateTime;

public class RegistrarTransaccionService implements RegistrarTransaccionUseCase {

    private final TransaccionRepositoryPort repository;
    private final TransaccionMapper mapper;

    public RegistrarTransaccionService(TransaccionRepositoryPort repository, TransaccionMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    @Override
    public TransaccionResponseDTO registrarTransaccion(TransaccionRequestDTO transaccionRequestDTO) {
        Transacciones transaccion = mapper.toDomain(transaccionRequestDTO);
        transaccion.setDateTime(LocalDateTime.now());
        return mapper.toDTO(repository.createTransaccion(transaccion));
    }
}

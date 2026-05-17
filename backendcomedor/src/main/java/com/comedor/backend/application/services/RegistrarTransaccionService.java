package com.comedor.backend.application.services;

import com.comedor.backend.application.common.mapper.TransactionMapper;
import com.comedor.backend.application.ports.in.RegistrarTransaccionUseCase;
import com.comedor.backend.application.ports.out.TransactionRepositoryPort;
import com.comedor.backend.domain.model.Transactions;
import com.comedor.backend.domain.model.enums.TipoMovimiento;
import com.comedor.backend.infrastructure.adapters.in.web.dto.request.TransaccionRequestDTO;
import com.comedor.backend.infrastructure.adapters.in.web.dto.response.TransaccionResponseDTO;

import java.time.LocalDateTime;

public class RegistrarTransaccionService implements RegistrarTransaccionUseCase {

    private final TransactionRepositoryPort repository;
    private final TransactionMapper mapper;

    public RegistrarTransaccionService(TransactionRepositoryPort repository, TransactionMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    @Override
    public TransaccionResponseDTO registrarTransaccion(TransaccionRequestDTO transaccionRequestDTO) {
        Transactions transaccion = mapper.toDomain(transaccionRequestDTO);
        transaccion.setDateTime(LocalDateTime.now());
        TipoMovimiento type = transaccion.getType();
        if (type==TipoMovimiento.ENTRADA)
        {
            transaccion.setFinalStock(transaccion.getCurrentStock().add(transaccion.getAmount()));
        }else
        {
            transaccion.setFinalStock(transaccion.getCurrentStock().subtract(transaccion.getAmount()));
        }
        return mapper.toDTO(repository.createTransaccion(transaccion));
    }
}

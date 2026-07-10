package com.comedor.backend.application.services;

import com.comedor.backend.application.common.mapper.TransactionMapper;
import com.comedor.backend.application.ports.in.RegisterTransactionUseCase;
import com.comedor.backend.application.ports.out.ProductRepositoryPort;
import com.comedor.backend.application.ports.out.TransactionRepositoryPort;
import com.comedor.backend.domain.model.Transactions;
import com.comedor.backend.domain.model.enums.MovementType;
import com.comedor.backend.infrastructure.adapters.in.web.dto.request.TransactionRequestDTO;
import com.comedor.backend.infrastructure.adapters.in.web.dto.response.TransactionResponseDTO;

public class RegisterTransactionService implements RegisterTransactionUseCase {

    private final TransactionRepositoryPort repository;
    private final TransactionMapper mapper;

    public RegisterTransactionService(TransactionRepositoryPort repository, TransactionMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    @Override
    public TransactionResponseDTO registrarTransaccion(TransactionRequestDTO transactionRequestDTO) {
        Transactions transaccion = mapper.toDomain(transactionRequestDTO);
        System.out.println(transaccion.toString());
        transaccion.setDateTime(transactionRequestDTO.getDateTime());
        transaccion.setCurrentStock(transactionRequestDTO.getCurrentStock());

        MovementType type = transaccion.getType();
        if (type== MovementType.ENTRADA)
        {
            transaccion.setFinalStock(transaccion.getCurrentStock().add(transaccion.getAmount()));
        }else
        {
            transaccion.setFinalStock(transaccion.getCurrentStock().subtract(transaccion.getAmount()));
        }
        return mapper.toDTO(repository.createTransaccion(transaccion));
    }
}

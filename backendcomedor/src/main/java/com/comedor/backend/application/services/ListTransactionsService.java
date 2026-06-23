package com.comedor.backend.application.services;

import com.comedor.backend.application.common.mapper.TransactionMapper;
import com.comedor.backend.application.ports.in.ListTransactionsUseCase;
import com.comedor.backend.application.ports.out.TransactionRepositoryPort;
import com.comedor.backend.domain.model.enums.TransactionSource;
import com.comedor.backend.domain.model.enums.MovementType;
import com.comedor.backend.infrastructure.adapters.in.web.dto.response.TransactionResponseDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.time.LocalDate;

public class ListTransactionsService implements ListTransactionsUseCase {
    private final TransactionRepositoryPort repository;
    private final TransactionMapper mapper;

    public ListTransactionsService(TransactionRepositoryPort repository, TransactionMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    @Override
    public Page<TransactionResponseDTO> list(int page, int size,
                                             LocalDate startDate,
                                             LocalDate endDate,
                                             MovementType type,
                                             TransactionSource source,
                                             String productName) {
        Pageable pageable = PageRequest.of(
                page,
                size,
                Sort.by(
                        Sort.Order.desc("dateTime"),
                        Sort.Order.desc("id")
                )
        );

        return repository.showTransacciones(startDate,
                endDate,
                type,
                source,
                productName,
                pageable)
                .map(mapper::toDTO);
    }
}

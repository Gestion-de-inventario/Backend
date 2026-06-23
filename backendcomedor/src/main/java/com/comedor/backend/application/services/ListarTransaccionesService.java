package com.comedor.backend.application.services;

import com.comedor.backend.application.common.mapper.TransactionMapper;
import com.comedor.backend.application.ports.in.ListarTransaccionesUseCase;
import com.comedor.backend.application.ports.out.TransactionRepositoryPort;
import com.comedor.backend.domain.model.enums.FuenteTransaccion;
import com.comedor.backend.domain.model.enums.TipoMovimiento;
import com.comedor.backend.infrastructure.adapters.in.web.dto.response.TransaccionResponseDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.time.LocalDate;
import java.util.List;

public class ListarTransaccionesService implements ListarTransaccionesUseCase {
    private final TransactionRepositoryPort repository;
    private final TransactionMapper mapper;

    public ListarTransaccionesService(TransactionRepositoryPort repository, TransactionMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    @Override
    public Page<TransaccionResponseDTO> list(int page, int size,
                                             LocalDate startDate,
                                             LocalDate endDate,
                                             TipoMovimiento type,
                                             FuenteTransaccion source,
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

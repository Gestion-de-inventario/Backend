package com.comedor.backend.application.services;

import com.comedor.backend.application.common.mapper.OrderInMapper;
import com.comedor.backend.application.ports.in.ListOrderInsUseCase;
import com.comedor.backend.application.ports.out.OrderInRepositoryPort;
import com.comedor.backend.domain.model.enums.EstadoOrden;
import com.comedor.backend.domain.model.enums.FuenteProducto;
import com.comedor.backend.infrastructure.adapters.in.web.dto.response.OrderInResponseDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.time.LocalDate;

public class ListOrderInsService implements ListOrderInsUseCase {

    private final OrderInRepositoryPort orderInRepositoryPort;
    private final OrderInMapper orderInMapper;

    public ListOrderInsService(
            OrderInRepositoryPort orderInRepositoryPort,
            OrderInMapper orderInMapper
    ) {
        this.orderInRepositoryPort = orderInRepositoryPort;
        this.orderInMapper = orderInMapper;
    }

    @Override
    public Page<OrderInResponseDTO> list(
            int page,
            int size,
            LocalDate startDate,
            LocalDate endDate,
            FuenteProducto source,
            EstadoOrden status
    ) {
        if (
                startDate != null &&
                        endDate != null &&
                        startDate.isAfter(endDate)
        ) {
            throw new IllegalArgumentException(
                    "La fecha de inicio no puede ser mayor que la fecha fin"
            );
        }

        Pageable pageable = PageRequest.of(
                page,
                size,
                Sort.by(
                        Sort.Order.desc("date"),
                        Sort.Order.desc("reference")
                )
        );

        return orderInRepositoryPort
                .showOrderIns(
                        startDate,
                        endDate,
                        source,
                        status,
                        pageable
                ).map(orderInMapper::toResponse);
    }
}
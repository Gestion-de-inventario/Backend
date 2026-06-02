package com.comedor.backend.application.services;

import com.comedor.backend.application.common.mapper.PurchaseMapper;
import com.comedor.backend.application.ports.in.ListPurchaseUseCase;
import com.comedor.backend.application.ports.out.PurchaseRepositoryPort;
import com.comedor.backend.infrastructure.adapters.in.web.dto.response.PurchaseResponseDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

public class ListPurchaseService implements ListPurchaseUseCase {
    private final PurchaseRepositoryPort repository;
    private final PurchaseMapper mapper;

    public ListPurchaseService(PurchaseRepositoryPort repository, PurchaseMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }


    @Override
    public Page<PurchaseResponseDTO> list(int page, int size) {
        Pageable pageable = PageRequest.of(
                page,
                size,
                Sort.by(
                        Sort.Order.desc("purchaseDate"),
                        Sort.Order.desc("id")
                )
        );

        return repository
                .showPurchase(pageable)
                .map(mapper::toResponse);
    }
}

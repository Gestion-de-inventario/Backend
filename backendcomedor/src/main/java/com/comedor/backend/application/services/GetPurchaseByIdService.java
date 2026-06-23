package com.comedor.backend.application.services;

import com.comedor.backend.application.common.mapper.PurchaseMapper;
import com.comedor.backend.application.ports.in.GetPurchaseByIdUseCase;
import com.comedor.backend.application.ports.out.PurchaseRepositoryPort;
import com.comedor.backend.infrastructure.adapters.in.web.dto.response.PurchaseResponseDTO;

public class GetPurchaseByIdService implements GetPurchaseByIdUseCase {
    private final PurchaseRepositoryPort repository;
    private final PurchaseMapper mapper;

    public GetPurchaseByIdService(PurchaseRepositoryPort repository, PurchaseMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    @Override
    public PurchaseResponseDTO getById(Integer id) {
        return mapper.toResponse(repository.findById(id));
    }
}

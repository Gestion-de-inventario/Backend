package com.comedor.backend.application.services;

import com.comedor.backend.application.common.mapper.DonationMapper;
import com.comedor.backend.application.ports.in.GetDonationByIdUseCase;
import com.comedor.backend.application.ports.out.DonationRepositoryPort;
import com.comedor.backend.infrastructure.adapters.in.web.dto.response.DonationResponseDTO;

public class GetDonationByIdService implements GetDonationByIdUseCase {
    private final DonationRepositoryPort repository;
    private final DonationMapper mapper;

    public GetDonationByIdService(DonationRepositoryPort repository, DonationMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    @Override
    public DonationResponseDTO getById(Integer id) {
        return mapper.toResponse(repository.findById(id));
    }
}

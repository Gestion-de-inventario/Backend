package com.comedor.backend.application.services;

import com.comedor.backend.application.common.mapper.DonationMapper;
import com.comedor.backend.application.ports.in.ListDonationUseCase;
import com.comedor.backend.application.ports.out.DonationRepositoryPort;
import com.comedor.backend.domain.model.enums.StatusOrder;
import com.comedor.backend.infrastructure.adapters.in.web.dto.response.DonationResponseDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.time.LocalDate;

public class ListDonationService implements ListDonationUseCase {

    private final DonationRepositoryPort repository;
    private final DonationMapper mapper;

    public ListDonationService(DonationRepositoryPort repository, DonationMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    @Override
    public Page<DonationResponseDTO> list(int page, int size, LocalDate startDate, LocalDate endDate, StatusOrder status) {
        Pageable pageable = PageRequest.of(
                page,
                size,
                Sort.by(
                        Sort.Order.desc("donationDate"),
                        Sort.Order.desc("id")
                )
        );

        return repository.showDonation(
                startDate,
                endDate,
                status,
                pageable
        ).map(mapper::toResponse);
    }
}

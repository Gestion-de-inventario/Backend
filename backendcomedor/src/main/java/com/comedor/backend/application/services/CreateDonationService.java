package com.comedor.backend.application.services;

import com.comedor.backend.application.common.mapper.DonationMapper;
import com.comedor.backend.application.ports.in.CreateDonationUseCase;
import com.comedor.backend.application.ports.out.DonationRepositoryPort;
import com.comedor.backend.application.ports.out.ProductRepositoryPort;
import com.comedor.backend.domain.exceptions.DateException;
import com.comedor.backend.domain.model.Donation;
import com.comedor.backend.domain.model.DonationDetail;
import com.comedor.backend.domain.model.Product;
import com.comedor.backend.domain.model.enums.EstadoOrden;
import com.comedor.backend.infrastructure.adapters.in.web.dto.request.CreateDonationDetailRequestDTO;
import com.comedor.backend.infrastructure.adapters.in.web.dto.request.CreateDonationRequestDTO;
import com.comedor.backend.infrastructure.adapters.in.web.dto.response.DonationResponseDTO;
import com.comedor.backend.infrastructure.config.PeruTime;

import java.util.ArrayList;
import java.util.List;

public class CreateDonationService implements CreateDonationUseCase {
    private final DonationRepositoryPort repository;
    private final DonationMapper mapper;
    private final ProductRepositoryPort productRepository;

    public CreateDonationService(DonationRepositoryPort repository, DonationMapper mapper, ProductRepositoryPort productRepository) {
        this.repository = repository;
        this.mapper = mapper;
        this.productRepository = productRepository;
    }

    @Override
    public DonationResponseDTO create(CreateDonationRequestDTO request) {

        List<DonationDetail> details = new ArrayList<>();

        for (CreateDonationDetailRequestDTO item: request.getDetails()) {
            Product product = productRepository
                    .getProductoById(item.getProductId());

            DonationDetail detail = createDonationDetail(
                    product,item);

            details.add(detail);
        }
        Donation donation = new Donation();
        //CUEVA refactor fecha
        //donation.setDonationDate(PeruTime.today());
        if(request.getDate().isBefore(PeruTime.today())  )
        {
            throw new DateException("Error al crear orden : La fecha de creación no puede ser menor a la actual ");
        }
        donation.setDonationDate(request.getDate());
        donation.setStatus(EstadoOrden.PENDIENTE);
        donation.setDetails(details);

        Donation savedDonation = repository.save(donation);

        return mapper.toResponse(savedDonation);
    }


    public DonationDetail createDonationDetail(Product product,
                                               CreateDonationDetailRequestDTO item) {
        DonationDetail detail = new DonationDetail();

        detail.setProduct(product);

        detail.setQuantity(item.getQuantity());

        return detail;
    }
}

package com.comedor.backend.infrastructure.adapters.out.persistence;

import com.comedor.backend.application.ports.out.DonationRepositoryPort;
import com.comedor.backend.domain.exceptions.DonationOrderNotFoundException;
import com.comedor.backend.domain.model.Donation;
import com.comedor.backend.domain.model.enums.StatusOrder;
import com.comedor.backend.infrastructure.adapters.out.persistence.entity.DonationEntity;
import com.comedor.backend.infrastructure.adapters.out.persistence.mapper.DonationEntityMapper;
import com.comedor.backend.infrastructure.adapters.out.persistence.repository.DonationJpaRepository;
import com.comedor.backend.infrastructure.adapters.out.persistence.repository.specification.DonationSpecification;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
@Component
@RequiredArgsConstructor
public class DonationRepositoryAdapter implements DonationRepositoryPort {
    private final DonationJpaRepository repository;
    private final DonationEntityMapper mapper;


    @Override
    public Donation save(Donation donation) {

        DonationEntity donationEntity = mapper.toEntity(donation);
        DonationEntity savedEntity = repository.save(donationEntity);
        return mapper.toDomain(savedEntity);
    }

    @Override
    public Page<Donation> showDonation(LocalDate startDate,
                                       LocalDate endDate,
                                       StatusOrder status,
                                       Pageable pageable)
    {
        if (
                startDate != null &&
                        endDate != null &&
                        startDate.isAfter(endDate)
        ) {
            throw new IllegalArgumentException(
                    "La fecha de inicio no puede ser mayor que la fecha fin"
            );
        }
        Specification<DonationEntity> spec =
                (root, query, cb) -> cb.conjunction();

        if(startDate != null){
            spec = spec.and(
                    DonationSpecification
                            .purchaseDateAfter(startDate)
            );
        }

        if(endDate != null){
            spec = spec.and(
                    DonationSpecification
                            .purchaseDateBefore(endDate)
            );
        }

        if(status != null){
            spec = spec.and(
                    DonationSpecification
                            .hasStatus(status)
            );
        }

        return repository.findAll(spec,pageable).map(mapper::toDomain);
    }

    @Override
    public Donation findById(Integer id) {
        DonationEntity entity = repository.findById(id).orElseThrow(
                ()-> new DonationOrderNotFoundException("Orden de donación no encontrada")
        );
        return mapper.toDomain(entity);
    }

    @Override
    public Donation changeStatus(Integer id, StatusOrder status) {
        DonationEntity entity = repository.findById(id).orElseThrow(
                ()-> new DonationOrderNotFoundException("Orden de donación no encontrada")
        );
        entity.setStatus(status);

        DonationEntity entitySaved = repository.save(entity);

        return mapper.toDomain(entitySaved);
    }
}

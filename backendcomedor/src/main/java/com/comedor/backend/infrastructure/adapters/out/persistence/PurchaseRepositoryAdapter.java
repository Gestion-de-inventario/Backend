package com.comedor.backend.infrastructure.adapters.out.persistence;

import com.comedor.backend.application.ports.out.PurchaseRepositoryPort;
import com.comedor.backend.domain.exceptions.PurchaseOrderNotFoundException;
import com.comedor.backend.domain.model.Purchase;
import com.comedor.backend.domain.model.enums.StatusOrder;
import com.comedor.backend.infrastructure.adapters.out.persistence.repository.specification.PurchaseSpecification;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import com.comedor.backend.infrastructure.adapters.out.persistence.entity.PurchaseEntity;
import com.comedor.backend.infrastructure.adapters.out.persistence.mapper.PurchaseEntityMapper;
import com.comedor.backend.infrastructure.adapters.out.persistence.repository.PurchaseJpaRepository;

import java.time.LocalDate;


@Component
@RequiredArgsConstructor
public class PurchaseRepositoryAdapter implements PurchaseRepositoryPort {

    private final PurchaseJpaRepository purchaseJpaRepository;

    private final PurchaseEntityMapper purchaseEntityMapper;

    @Override
    public Purchase save(Purchase purchase) {

        PurchaseEntity entity =
                purchaseEntityMapper.toEntity(purchase);

        PurchaseEntity savedEntity =
                purchaseJpaRepository.save(entity);

        return purchaseEntityMapper.toDomain(savedEntity);
    }

    @Override
    public Page<Purchase> showPurchase(
            LocalDate startDate,
            LocalDate endDate,
            StatusOrder status,
            Pageable pageable
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

        Specification<PurchaseEntity> spec =
                (root, query, cb) -> cb.conjunction();

        if(startDate != null){
            spec = spec.and(
                    PurchaseSpecification
                            .purchaseDateAfter(startDate)
            );
        }

        if(endDate != null){
            spec = spec.and(
                    PurchaseSpecification
                            .purchaseDateBefore(endDate)
            );
        }

        if(status != null){
            spec = spec.and(
                    PurchaseSpecification
                            .hasStatus(status)
            );
        }

        return purchaseJpaRepository
                .findAll(spec, pageable)
                .map(purchaseEntityMapper::toDomain);
    }

    @Override
    public Purchase findById(Integer id) {
        PurchaseEntity entity = purchaseJpaRepository
                .findById(id)
                .orElseThrow(() ->
                        new PurchaseOrderNotFoundException(
                                "Orden de compra no encontrada"
                        )
                );

        return purchaseEntityMapper.toDomain(entity);
    }

    @Override
    public Purchase updateStatus(Integer purchaseId,
                                 StatusOrder status) {

        PurchaseEntity entity =
                purchaseJpaRepository.findById(purchaseId)
                        .orElseThrow(() ->
                                new PurchaseOrderNotFoundException(
                                        "Orden de compra no encontrada"
                                )
                        );

        entity.setStatus(status);

        PurchaseEntity saved =
                purchaseJpaRepository.save(entity);

        return purchaseEntityMapper.toDomain(saved);
    }
}

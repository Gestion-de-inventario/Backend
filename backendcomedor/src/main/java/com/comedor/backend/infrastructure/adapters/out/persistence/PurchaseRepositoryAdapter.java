package com.comedor.backend.infrastructure.adapters.out.persistence;

import com.comedor.backend.application.ports.out.PurchaseRepositoryPort;
import com.comedor.backend.domain.model.Purchase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;


import com.comedor.backend.application.ports.out.PurchaseRepositoryPort;
import com.comedor.backend.domain.model.Purchase;
import com.comedor.backend.infrastructure.adapters.out.persistence.entity.PurchaseEntity;
import com.comedor.backend.infrastructure.adapters.out.persistence.mapper.PurchaseEntityMapper;
import com.comedor.backend.infrastructure.adapters.out.persistence.repository.PurchaseJpaRepository;


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
}

package com.comedor.backend.infrastructure.adapters.out.persistence;

import com.comedor.backend.application.ports.out.PurchaseDetailRepositoryPort;
import com.comedor.backend.domain.model.PurchaseDetail;
import com.comedor.backend.infrastructure.adapters.out.persistence.entity.PurchaseDetailEntity;
import com.comedor.backend.infrastructure.adapters.out.persistence.mapper.PurchaseDetailEntityMapper;
import com.comedor.backend.infrastructure.adapters.out.persistence.repository.PurchaseDetailJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class PurchaseDetailRepositoryAdapter implements PurchaseDetailRepositoryPort {

    private final PurchaseDetailJpaRepository repository;
    private final PurchaseDetailEntityMapper mapper;

    @Override
    public List<PurchaseDetail> findAvailableByProduct(Integer productId) {
        return repository.findAvailableByProductIdOrderByPurchaseDateAsc(productId)
                .stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public PurchaseDetail update(PurchaseDetail detail) {
        PurchaseDetailEntity entity = mapper.toEntity(detail);
        return mapper.toDomain(repository.save(entity));
    }
}
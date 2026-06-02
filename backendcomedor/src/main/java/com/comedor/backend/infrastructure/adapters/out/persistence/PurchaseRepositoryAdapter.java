package com.comedor.backend.infrastructure.adapters.out.persistence;

import com.comedor.backend.application.ports.out.PurchaseRepositoryPort;
import com.comedor.backend.domain.exceptions.OrdenDeCompraNoEncontrada;
import com.comedor.backend.domain.model.Purchase;
import com.comedor.backend.domain.model.enums.EstadoOrden;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

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

    @Override
    public Page<Purchase> showPurchase(Pageable pageable) {
        return purchaseJpaRepository
                .findAll(pageable)
                .map(purchaseEntityMapper::toDomain);
    }

    @Override
    public Purchase findById(Integer id) {
        PurchaseEntity entity = purchaseJpaRepository
                .findById(id)
                .orElseThrow(() ->
                        new OrdenDeCompraNoEncontrada(
                                "Orden de compra no encontrada"
                        )
                );

        return purchaseEntityMapper.toDomain(entity);
    }

    @Override
    public Purchase updateStatus(Integer purchaseId,
                                 EstadoOrden status) {

        PurchaseEntity entity =
                purchaseJpaRepository.findById(purchaseId)
                        .orElseThrow(() ->
                                new OrdenDeCompraNoEncontrada(
                                        "Orden de compra no encontrada"
                                )
                        );

        entity.setStatus(status);

        PurchaseEntity saved =
                purchaseJpaRepository.save(entity);

        return purchaseEntityMapper.toDomain(saved);
    }
}

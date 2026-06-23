package com.comedor.backend.infrastructure.adapters.out.persistence;

import com.comedor.backend.application.ports.out.InventoryLotRepositoryPort;
import com.comedor.backend.domain.model.InventoryLot;
import com.comedor.backend.infrastructure.adapters.out.persistence.entity.InventoryLotEntity;
import com.comedor.backend.infrastructure.adapters.out.persistence.mapper.InventoryLotEntityMapper;
import com.comedor.backend.infrastructure.adapters.out.persistence.repository.InventoryLotJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
@Component
@RequiredArgsConstructor
public class InventoryLotRepositoryAdapter implements InventoryLotRepositoryPort {
    private final InventoryLotJpaRepository repository;

    private final InventoryLotEntityMapper mapper;

    @Override
    public List<InventoryLot> findAvailableByProduct(Integer productId) {

        return repository
                .findAvailableByProductIdOrderByFifo(productId)
                .stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public InventoryLot update(InventoryLot inventoryLot) {

        InventoryLotEntity entity =
                repository.save(
                        mapper.toEntity(inventoryLot)
                );

        return mapper.toDomain(entity);
    }

    @Override
    public InventoryLot create(InventoryLot inventoryLot) {

        InventoryLotEntity entity =
                repository.save(
                        mapper.toEntity(inventoryLot)
                );

        return mapper.toDomain(entity);
    }

    @Override
    public InventoryLot findById(Integer id) {

        InventoryLotEntity entity =
                repository.findById(id)
                        .orElseThrow(
                                () -> new RuntimeException(
                                        "Lote no encontrado"
                                )
                        );

        return mapper.toDomain(entity);
    }
}

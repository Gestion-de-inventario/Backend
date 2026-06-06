package com.comedor.backend.infrastructure.adapters.out.persistence.mapper;

import com.comedor.backend.domain.model.StockMovement;
import com.comedor.backend.infrastructure.adapters.out.persistence.entity.StockMovementEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
public class StockMovementEntityMapper {


    private final PurchaseEntityMapper purchaseMapper;
    private final InventoryLotEntityMapper inventoryMapper;
    public StockMovementEntityMapper(PurchaseEntityMapper purchaseMapper, InventoryLotEntityMapper inventoryMapper) {
        this.purchaseMapper = purchaseMapper;
        this.inventoryMapper = inventoryMapper;
    }

    public StockMovement toDomain(StockMovementEntity entity) {
        if (entity == null) {
            return null;
        }

        StockMovement domain = new StockMovement();
        domain.setId(entity.getId());


        if (entity.getInventoryLot() != null) {
            domain.setInventoryLot(inventoryMapper.toDomain(entity.getInventoryLot()));
        }

        /* * NOTA: Evitamos mapear el MenuReport hacia el dominio aquí para
         * prevenir un ciclo infinito de llamadas entre mappers y un StackOverflowError.
         */

        domain.setQuantityUsed(entity.getQuantityUsed());
        domain.setUnitCost(entity.getUnitCost());
        domain.setTotalCost(entity.getTotalCost());
        domain.setMovementDate(entity.getMovementDate());

        return domain;
    }

    public StockMovementEntity toEntity(StockMovement domain) {
        if (domain == null) {
            return null;
        }

        StockMovementEntity entity = new StockMovementEntity();
        entity.setId(domain.getId());


        if (domain.getInventoryLot() != null) {
            entity.setInventoryLot(inventoryMapper.toEntity(domain.getInventoryLot()));
        }

        entity.setQuantityUsed(domain.getQuantityUsed());
        entity.setUnitCost(domain.getUnitCost());
        entity.setTotalCost(domain.getTotalCost());
        entity.setMovementDate(domain.getMovementDate());

        return entity;
    }
}
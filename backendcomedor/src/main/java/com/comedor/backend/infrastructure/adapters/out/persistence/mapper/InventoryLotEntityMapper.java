package com.comedor.backend.infrastructure.adapters.out.persistence.mapper;

import com.comedor.backend.domain.model.InventoryLot;
import com.comedor.backend.infrastructure.adapters.out.persistence.entity.InventoryLotEntity;
import org.springframework.stereotype.Component;

@Component
public class InventoryLotEntityMapper {

    private final ProductEntityMapper productMapper;

    public InventoryLotEntityMapper(ProductEntityMapper productMapper) {
        this.productMapper = productMapper;
    }

    public InventoryLot toDomain(InventoryLotEntity entity) {

        if (entity == null) {
            return null;
        }

        InventoryLot domain = new InventoryLot();

        domain.setId(entity.getId());
        domain.setProduct(
                productMapper.toDomain(entity.getProduct())
        );
        domain.setQuantity(entity.getQuantity());
        domain.setRemainingQuantity(
                entity.getRemainingQuantity()
        );
        domain.setUnitCost(entity.getUnitCost());
        domain.setEntryDate(entity.getEntryDate());

        return domain;
    }

    public InventoryLotEntity toEntity(InventoryLot domain) {

        if (domain == null) {
            return null;
        }

        InventoryLotEntity entity = new InventoryLotEntity();

        entity.setId(domain.getId());
        entity.setProduct(
                productMapper.toEntity(domain.getProduct())
        );
        entity.setQuantity(domain.getQuantity());
        entity.setRemainingQuantity(
                domain.getRemainingQuantity()
        );
        entity.setUnitCost(domain.getUnitCost());
        entity.setEntryDate(domain.getEntryDate());

        return entity;
    }
}

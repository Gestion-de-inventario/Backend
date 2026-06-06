package com.comedor.backend.application.ports.out;

import com.comedor.backend.domain.model.InventoryLot;

import java.util.List;

public interface InventoryLotRepositoryPort {
    List<InventoryLot> findAvailableByProduct(Integer productId);

    InventoryLot update(InventoryLot inventoryLot);

    InventoryLot create(InventoryLot inventoryLot);

    InventoryLot findById(Integer id);
}

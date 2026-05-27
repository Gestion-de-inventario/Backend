package com.comedor.backend.application.ports.out;

import com.comedor.backend.domain.model.Purchase;

public interface PurchaseRepositoryPort {
    Purchase save(Purchase purchase);
}

package com.comedor.backend.application.ports.out;

import com.comedor.backend.domain.model.PurchaseDetail;
import java.util.List;

public interface PurchaseDetailRepositoryPort {
    List<PurchaseDetail> findAvailableByProduct(Integer productId);

    PurchaseDetail update(PurchaseDetail detail);
}
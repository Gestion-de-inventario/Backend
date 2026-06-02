package com.comedor.backend.application.ports.out;

import com.comedor.backend.domain.model.Purchase;
import com.comedor.backend.domain.model.PurchaseDetail;
import com.comedor.backend.domain.model.enums.EstadoOrden;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface PurchaseRepositoryPort {
    Purchase save(Purchase purchase);
    Page<Purchase> showPurchase(Pageable pageable);
    Purchase findById(Integer id);
    Purchase updateStatus(Integer purchaseId,EstadoOrden status);
}

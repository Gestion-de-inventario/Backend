package com.comedor.backend.application.ports.out;

import com.comedor.backend.domain.model.Purchase;
import com.comedor.backend.domain.model.enums.StatusOrder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;

public interface PurchaseRepositoryPort {
    Purchase save(Purchase purchase);
    Page<Purchase> showPurchase(
            LocalDate startDate,
            LocalDate endDate,
            StatusOrder status,
            Pageable pageable
    );
    Purchase findById(Integer id);
    Purchase updateStatus(Integer purchaseId, StatusOrder status);
}

package com.comedor.backend.infrastructure.adapters.out.persistence.repository.specification;

import com.comedor.backend.domain.model.enums.StatusOrder;
import com.comedor.backend.infrastructure.adapters.out.persistence.entity.PurchaseEntity;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;

public class PurchaseSpecification {

    public static Specification<PurchaseEntity> purchaseDateAfter(
            LocalDate startDate
    ) {
        return (root, query, cb) ->
                cb.greaterThanOrEqualTo(
                        root.get("purchaseDate"),
                        startDate
                );
    }

    public static Specification<PurchaseEntity> purchaseDateBefore(
            LocalDate endDate
    ) {
        return (root, query, cb) ->
                cb.lessThanOrEqualTo(
                        root.get("purchaseDate"),
                        endDate
                );
    }

    public static Specification<PurchaseEntity> hasStatus(
            StatusOrder status
    ) {
        return (root, query, cb) ->
                cb.equal(
                        root.get("status"),
                        status
                );
    }
}
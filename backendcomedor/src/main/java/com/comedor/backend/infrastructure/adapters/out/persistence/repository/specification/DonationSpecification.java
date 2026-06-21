package com.comedor.backend.infrastructure.adapters.out.persistence.repository.specification;

import com.comedor.backend.domain.model.enums.EstadoOrden;
import com.comedor.backend.infrastructure.adapters.out.persistence.entity.DonationEntity;
import com.comedor.backend.infrastructure.adapters.out.persistence.entity.PurchaseEntity;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;

public class DonationSpecification {

    public static Specification<DonationEntity> purchaseDateAfter(
            LocalDate startDate
    ) {
        return (root, query, cb) ->
                cb.greaterThanOrEqualTo(
                        root.get("donationDate"),
                        startDate
                );
    }

    public static Specification<DonationEntity> purchaseDateBefore(
            LocalDate endDate
    ) {
        return (root, query, cb) ->
                cb.lessThanOrEqualTo(
                        root.get("donationDate"),
                        endDate
                );
    }

    public static Specification<DonationEntity> hasStatus(
            EstadoOrden status
    ) {
        return (root, query, cb) ->
                cb.equal(
                        root.get("status"),
                        status
                );
    }

}

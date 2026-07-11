package com.comedor.backend.infrastructure.adapters.out.persistence.repository.specification;

import com.comedor.backend.domain.model.enums.TransactionReferenceType;
import com.comedor.backend.domain.model.enums.TransactionSource;
import com.comedor.backend.domain.model.enums.MovementType;
import com.comedor.backend.infrastructure.adapters.out.persistence.entity.TransactionsEntity;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;
import java.time.LocalTime;

public class TransactionSpecification {
    public static Specification<TransactionsEntity> dateAfter(LocalDate startDate) {
        return (root, query, cb) -> {
            if (startDate == null) return null;

            return cb.greaterThanOrEqualTo(
                    root.get("dateTime"),
                    startDate.atStartOfDay()
            );
        };
    }

    public static Specification<TransactionsEntity> dateBefore(LocalDate endDate) {
        return (root, query, cb) -> {
            if (endDate == null) return null;

            return cb.lessThanOrEqualTo(
                    root.get("dateTime"),
                    endDate.atTime(LocalTime.MAX)
            );
        };
    }

    public static Specification<TransactionsEntity> hasType(MovementType type) {
        return (root, query, cb) -> {
            if (type == null) return null;

            return cb.equal(root.get("type"), type);
        };
    }

    public static Specification<TransactionsEntity> hasSource(TransactionSource source) {
        return (root, query, cb) -> {
            if (source == null) return null;

            return cb.equal(root.get("source"), source);
        };
    }

    public static Specification<TransactionsEntity> hasTransactionReferenceType(TransactionReferenceType referenceType) {
        return (root, query, cb) -> {
            if (referenceType == null) return null;

            return cb.equal(root.get("referenceType"), referenceType);
        };
    }

    public static Specification<TransactionsEntity> itemNameLike(String itemName) {
        return (root, query, cb) -> {
            if (itemName == null || itemName.isBlank()) {
                return null;
            }

            String itemNameNormalized = itemName.trim().toLowerCase();

            return cb.like(
                    cb.lower(root.<String>get("itemName")),
                    "%" + itemNameNormalized + "%"
            );
        };
    }
}

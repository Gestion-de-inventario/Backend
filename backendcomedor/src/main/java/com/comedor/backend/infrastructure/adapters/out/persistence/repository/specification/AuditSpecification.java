package com.comedor.backend.infrastructure.adapters.out.persistence.repository.specification;

import com.comedor.backend.domain.model.enums.AuditAction;
import com.comedor.backend.infrastructure.adapters.out.persistence.entity.AuditEntity;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;

public class AuditSpecification {

    public static Specification<AuditEntity> dateTimeAfter(
            LocalDate startDate
    ) {
        return (root, query, cb) ->
                cb.greaterThanOrEqualTo(
                        root.get("dateTime"),
                        startDate.atStartOfDay()
                );
    }

    public static Specification<AuditEntity> dateTimeBefore(
            LocalDate endDate
    ) {
        return (root, query, cb) ->
                cb.lessThan(
                        root.get("dateTime"),
                        endDate.plusDays(1).atStartOfDay()
                );
    }

    public static Specification<AuditEntity> hasAction(
            AuditAction action
    ) {
        return (root, query, cb) ->
                cb.equal(
                        root.get("action"),
                        action
                );
    }
}
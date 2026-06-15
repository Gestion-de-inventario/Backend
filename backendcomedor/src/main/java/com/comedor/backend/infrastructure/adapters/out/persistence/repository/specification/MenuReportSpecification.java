package com.comedor.backend.infrastructure.adapters.out.persistence.repository.specification;

import com.comedor.backend.infrastructure.adapters.out.persistence.entity.MenuReportEntity;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;

public class MenuReportSpecification {

    public static Specification<MenuReportEntity> reportDateAfter(
            LocalDate startDate
    ) {
        return (root, query, cb) ->
                cb.greaterThanOrEqualTo(
                        root.get("date"),
                        startDate
                );
    }

    public static Specification<MenuReportEntity> reportDateBefore(
            LocalDate endDate
    ) {
        return (root, query, cb) ->
                cb.lessThanOrEqualTo(
                        root.get("date"),
                        endDate
                );
    }
}

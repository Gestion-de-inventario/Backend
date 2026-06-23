package com.comedor.backend.infrastructure.adapters.out.persistence.repository.specification;

import com.comedor.backend.domain.model.enums.StatusOrder;
import com.comedor.backend.domain.model.enums.ProductSource;
import com.comedor.backend.infrastructure.adapters.out.persistence.entity.OrderInViewEntity;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;

public class OrderInSpecification {


    public static Specification<OrderInViewEntity> dateAfter(
            LocalDate startDate
    ){

        return (root, query, cb) ->
                cb.greaterThanOrEqualTo(
                        root.get("date"),
                        startDate
                );
    }



    public static Specification<OrderInViewEntity> dateBefore(
            LocalDate endDate
    ){

        return (root, query, cb) ->
                cb.lessThanOrEqualTo(
                        root.get("date"),
                        endDate
                );
    }



    public static Specification<OrderInViewEntity> hasSource(
            ProductSource source
    ) {
        return (root, query, cb) ->
                cb.equal(
                        root.get("source"),
                        source
                );
    }



    public static Specification<OrderInViewEntity> hasStatus(
            StatusOrder status
    ) {
        return (root, query, cb) ->
                cb.equal(
                        root.get("status"),
                        status
                );
    }

}
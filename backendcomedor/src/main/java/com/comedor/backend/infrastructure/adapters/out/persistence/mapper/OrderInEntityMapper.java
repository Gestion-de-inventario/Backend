package com.comedor.backend.infrastructure.adapters.out.persistence.mapper;

import com.comedor.backend.domain.model.OrderIn;
import com.comedor.backend.infrastructure.adapters.out.persistence.entity.OrderInViewEntity;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class OrderInEntityMapper {

    public OrderIn toDomain(OrderInViewEntity entity) {

        if (entity == null) {
            return null;
        }

        OrderIn orderIn = new OrderIn();

        orderIn.setReference(entity.getReference());
        orderIn.setId(entity.getId());
        orderIn.setSource(entity.getSource());
        orderIn.setDate(entity.getDate());
        orderIn.setStatus(entity.getStatus());
        orderIn.setTotalSpent(
                entity.getTotalSpent() != null
                        ? entity.getTotalSpent()
                        : BigDecimal.ZERO
        );

        return orderIn;
    }
}
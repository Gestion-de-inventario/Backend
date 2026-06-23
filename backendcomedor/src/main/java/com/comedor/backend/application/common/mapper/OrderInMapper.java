package com.comedor.backend.application.common.mapper;


import com.comedor.backend.domain.model.OrderIn;
import com.comedor.backend.infrastructure.adapters.in.web.dto.response.OrderInResponseDTO;
import com.comedor.backend.infrastructure.adapters.out.persistence.entity.OrderInViewEntity;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class OrderInMapper {

    public OrderInResponseDTO toResponse(OrderIn orderIn) {

        if (orderIn == null) {
            return null;
        }

        OrderInResponseDTO dto = new OrderInResponseDTO();

        dto.setReference(orderIn.getReference());
        dto.setId(orderIn.getId());
        dto.setSource(orderIn.getSource());
        dto.setDate(orderIn.getDate());
        dto.setStatus(orderIn.getStatus().name());
        dto.setTotalSpent(
                orderIn.getTotalSpent() != null
                        ? orderIn.getTotalSpent()
                        : BigDecimal.ZERO
        );

        return dto;
    }
}
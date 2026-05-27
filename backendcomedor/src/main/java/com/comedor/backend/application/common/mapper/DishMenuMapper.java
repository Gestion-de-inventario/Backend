package com.comedor.backend.application.common.mapper;

import com.comedor.backend.domain.model.DishMenu;
import com.comedor.backend.domain.model.DishSupply;
import com.comedor.backend.infrastructure.adapters.in.web.dto.response.DishMenuResponseDTO;
import com.comedor.backend.infrastructure.adapters.in.web.dto.response.DishSupplyResponseDTO;
import org.springframework.stereotype.Component;

@Component
public class DishMenuMapper {

    public DishMenuResponseDTO toResponse(DishMenu domain) {

        DishMenuResponseDTO dto =
                new DishMenuResponseDTO();

        dto.setId(domain.getId());

        dto.setName(domain.getName());

        dto.setStatus(domain.getStatus().name());

        dto.setSupplies(
                domain.getSupplies()
                        .stream()
                        .map(this::toSupplyResponse)
                        .toList()
        );

        return dto;
    }

    private DishSupplyResponseDTO toSupplyResponse(
            DishSupply domain
    ) {

        DishSupplyResponseDTO dto =
                new DishSupplyResponseDTO();

        dto.setProductId(
                domain.getProduct().getId()
        );

        dto.setProductName(
                domain.getProduct().getName()
        );

        dto.setQuantityNeeded(
                domain.getQuantityNeeded()
        );

        dto.setUnit(
                domain.getProduct().getUnit()
        );

        return dto;
    }
}

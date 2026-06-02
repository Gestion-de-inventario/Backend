package com.comedor.backend.application.common.mapper;

import com.comedor.backend.domain.model.StockMovement;
// IMPORTANTE: Asegúrate de importar tu DTO real aquí.
// Yo le puse StockMovementResponseDTO, pero si en tu proyecto
// se llama RegistroDTO o RegistroProductoResponseDTO, cámbialo.
import com.comedor.backend.infrastructure.adapters.in.web.dto.response.StockMovementResponseDTO;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class StockMovementMapper {

    public StockMovementResponseDTO toDto(StockMovement domain) {
        if (domain == null) {
            return null;
        }

        StockMovementResponseDTO dto = new StockMovementResponseDTO();

        dto.setId(domain.getId());

        // Mapeamos el nombre del producto si existe para que el frontend lo pueda mostrar
        if (domain.getProduct() != null) {
            dto.setProductName(domain.getProduct().getName());
            dto.setProductUnit(domain.getProduct().getUnit());
        }

        dto.setQuantityUsed(domain.getQuantityUsed());
        dto.setUnitCost(domain.getUnitCost());
        dto.setTotalCost(domain.getTotalCost());
        dto.setMovementDate(domain.getMovementDate());

        return dto;
    }

    public List<StockMovementResponseDTO> toListDto(List<StockMovement> domains) {
        if (domains == null || domains.isEmpty()) {
            return new ArrayList<>();
        }

        return domains.stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }
}
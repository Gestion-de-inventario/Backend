package com.comedor.backend.application.common.mapper;

import com.comedor.backend.domain.model.Product;
import com.comedor.backend.infrastructure.adapters.in.web.dto.response.AlertaStockResponseDTO;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class AlertaStockMapper {

    public AlertaStockResponseDTO toResponseDTO(Product product) {
        AlertaStockResponseDTO dto = new AlertaStockResponseDTO();
        dto.setId(product.getId());
        dto.setName(product.getName());
        dto.setUnit(product.getUnit());
        dto.setStock(product.getStock());
        dto.setReorderPoint(product.getReorderPoint());
        dto.setCategoria(product.getCategory() != null ? product.getCategory().getName() : null);
        return dto;
    }

    public List<AlertaStockResponseDTO> toResponseDTOList(List<Product> products) {
        return products.stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
    }
}

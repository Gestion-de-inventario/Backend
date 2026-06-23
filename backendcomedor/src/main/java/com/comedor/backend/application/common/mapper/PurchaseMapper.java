package com.comedor.backend.application.common.mapper;

import com.comedor.backend.domain.model.Product;
import com.comedor.backend.domain.model.Purchase;
import com.comedor.backend.domain.model.PurchaseDetail;
import com.comedor.backend.infrastructure.adapters.in.web.dto.request.CreatePurchaseDetailRequestDTO;
import com.comedor.backend.infrastructure.adapters.in.web.dto.response.PurchaseDetailResponseDTO;
import com.comedor.backend.infrastructure.adapters.in.web.dto.response.PurchaseResponseDTO;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class PurchaseMapper {

    public PurchaseResponseDTO toResponse(Purchase purchase) {

        PurchaseResponseDTO dto = new PurchaseResponseDTO();

        dto.setId(purchase.getId());
        dto.setPurchaseDate(purchase.getPurchaseDate());
        dto.setStatus(purchase.getStatus().name());
        dto.setTotalSpent(purchase.getTotalSpent());

        dto.setDetails(
                purchase.getDetails()
                        .stream()
                        .map(this::toDetailResponse)
                        .toList()
        );

        return dto;
    }

    private PurchaseDetailResponseDTO toDetailResponse(PurchaseDetail detail) {

        PurchaseDetailResponseDTO dto = new PurchaseDetailResponseDTO();

        dto.setProductId(detail.getProduct().getId());
        dto.setProductName(detail.getProduct().getName());
        dto.setProductUnit(detail.getProduct().getUnit());
        dto.setQuantity(detail.getQuantity());
        dto.setUnitPrice(detail.getUnitPrice());
        dto.setSubTotal(detail.getSubTotal());

        return dto;
    }

}
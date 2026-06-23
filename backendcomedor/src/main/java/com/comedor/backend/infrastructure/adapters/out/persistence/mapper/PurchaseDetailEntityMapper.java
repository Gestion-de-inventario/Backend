package com.comedor.backend.infrastructure.adapters.out.persistence.mapper;

import com.comedor.backend.domain.model.PurchaseDetail;
import com.comedor.backend.infrastructure.adapters.out.persistence.entity.PurchaseDetailEntity;
import org.springframework.stereotype.Component;

@Component
public class PurchaseDetailEntityMapper {

    private final PurchaseEntityMapper purchaseMapper;
    private final ProductEntityMapper productMapper;

    public PurchaseDetailEntityMapper(PurchaseEntityMapper purchaseMapper, ProductEntityMapper productMapper) {
        this.purchaseMapper = purchaseMapper;
        this.productMapper = productMapper;
    }

    public PurchaseDetail toDomain(PurchaseDetailEntity entity) {
        if (entity == null) return null;

        return new PurchaseDetail(
                entity.getId(),
                purchaseMapper.toDomain(entity.getPurchase()),
                productMapper.toDomain(entity.getProduct()),
                entity.getQuantity(),
                entity.getUnitPrice(),
                entity.getSubTotal()
        );
    }

    public PurchaseDetailEntity toEntity(PurchaseDetail domain) {
        if (domain == null) return null;

        PurchaseDetailEntity entity = new PurchaseDetailEntity();
        entity.setId(domain.getId());
        entity.setPurchase(purchaseMapper.toEntity(domain.getPurchase()));
        entity.setProduct(productMapper.toEntity(domain.getProduct()));
        entity.setQuantity(domain.getQuantity());
        entity.setUnitPrice(domain.getUnitPrice());
        entity.setSubTotal(domain.getSubTotal());

        return entity;
    }
}
package com.comedor.backend.infrastructure.adapters.out.persistence.mapper;

import com.comedor.backend.domain.model.Product;
import com.comedor.backend.domain.model.Purchase;
import com.comedor.backend.domain.model.PurchaseDetail;
import com.comedor.backend.infrastructure.adapters.out.persistence.entity.ProductEntity;
import com.comedor.backend.infrastructure.adapters.out.persistence.entity.PurchaseDetailEntity;
import com.comedor.backend.infrastructure.adapters.out.persistence.entity.PurchaseEntity;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class PurchaseEntityMapper {

    private final ProductEntityMapper productEntityMapper;

    public PurchaseEntityMapper(ProductEntityMapper productEntityMapper) {
        this.productEntityMapper = productEntityMapper;
    }

    public Purchase toDomain(PurchaseEntity entity) {

        if (entity == null) {
            return null;
        }

        Purchase purchase = new Purchase();

        purchase.setId(entity.getId());

        purchase.setPurchaseDate(entity.getPurchaseDate());

        purchase.setStatus(entity.getStatus());

        purchase.setTotalSpent(entity.getTotalSpent());

        purchase.setDetails(
                toDetailDomainList(entity.getDetails())
        );

        return purchase;
    }

    public PurchaseEntity toEntity(Purchase domain) {

        if (domain == null) {
            return null;
        }

        PurchaseEntity entity = new PurchaseEntity();

        entity.setId(domain.getId());

        entity.setPurchaseDate(domain.getPurchaseDate());

        entity.setStatus(domain.getStatus());

        entity.setTotalSpent(domain.getTotalSpent());

        List<PurchaseDetailEntity> detailEntities =
                toDetailEntityList(domain.getDetails());

        detailEntities.forEach(detail ->
                detail.setPurchase(entity)
        );

        entity.setDetails(detailEntities);

        return entity;
    }

    private List<PurchaseDetail> toDetailDomainList(
            List<PurchaseDetailEntity> entities
    ) {

        if (entities == null) {
            return new ArrayList<>();
        }

        return entities.stream()
                .map(this::toDetailDomain)
                .toList();
    }

    public PurchaseDetail toDetailDomain(
            PurchaseDetailEntity entity
    ) {

        PurchaseDetail detail = new PurchaseDetail();

        detail.setId(entity.getId());

        detail.setQuantity(entity.getQuantity());

        detail.setRemainingQuantity(entity.getRemainingQuantity());

        detail.setUnitPrice(entity.getUnitPrice());

        detail.setSubTotal(entity.getSubTotal());

        Product product =
                productEntityMapper.toDomain(entity.getProduct());

        detail.setProduct(product);

        return detail;
    }

    private List<PurchaseDetailEntity> toDetailEntityList(
            List<PurchaseDetail> domains
    ) {

        if (domains == null) {
            return new ArrayList<>();
        }

        return domains.stream()
                .map(this::toDetailEntity)
                .toList();
    }

    public  PurchaseDetailEntity toDetailEntity(
            PurchaseDetail domain
    ) {

        PurchaseDetailEntity entity =
                new PurchaseDetailEntity();

        entity.setId(domain.getId());

        entity.setQuantity(domain.getQuantity());

        entity.setRemainingQuantity(domain.getRemainingQuantity());

        entity.setUnitPrice(domain.getUnitPrice());

        entity.setSubTotal(domain.getSubTotal());

        ProductEntity productEntity =
                productEntityMapper.toEntity(domain.getProduct());

        entity.setProduct(productEntity);

        return entity;
    }
}

package com.comedor.backend.infrastructure.adapters.out.persistence.mapper;

import com.comedor.backend.domain.model.StockMovement;
import com.comedor.backend.infrastructure.adapters.out.persistence.entity.StockMovementEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
public class StockMovementEntityMapper {

    private final ProductEntityMapper productMapper;
    private final PurchaseEntityMapper purchaseMapper;

    public StockMovementEntityMapper(ProductEntityMapper productMapper, PurchaseEntityMapper purchaseMapper) {
        this.productMapper = productMapper;
        this.purchaseMapper = purchaseMapper;
    }

    public StockMovement toDomain(StockMovementEntity entity) {
        if (entity == null) {
            return null;
        }

        StockMovement domain = new StockMovement();
        domain.setId(entity.getId());

        if (entity.getProduct() != null) {
            domain.setProduct(productMapper.toDomain(entity.getProduct()));
        }

        if (entity.getPurchaseDetail() != null) {
            domain.setPurchaseDetail(purchaseMapper.toDetailDomain(entity.getPurchaseDetail()));
        }

        /* * NOTA: Evitamos mapear el MenuReport hacia el dominio aquí para
         * prevenir un ciclo infinito de llamadas entre mappers y un StackOverflowError.
         */

        domain.setQuantityUsed(entity.getQuantityUsed());
        domain.setUnitCost(entity.getUnitCost());
        domain.setTotalCost(entity.getTotalCost());
        domain.setMovementDate(entity.getMovementDate());

        return domain;
    }

    public StockMovementEntity toEntity(StockMovement domain) {
        if (domain == null) {
            return null;
        }

        StockMovementEntity entity = new StockMovementEntity();
        entity.setId(domain.getId());

        if (domain.getProduct() != null) {
            entity.setProduct(productMapper.toEntity(domain.getProduct()));
        }

        if (domain.getPurchaseDetail() != null) {
            entity.setPurchaseDetail(purchaseMapper.toDetailEntity(domain.getPurchaseDetail()));
        }

        /* * NOTA: El seteo del 'MenuReportEntity' dentro de esta entidad
         * YA SE HACE en el MenuReportEntityMapper (con entity.setMenuReport(...)).
         * Hacerlo aquí causaría inyección cíclica en Spring.
         */

        entity.setQuantityUsed(domain.getQuantityUsed());
        entity.setUnitCost(domain.getUnitCost());
        entity.setTotalCost(domain.getTotalCost());
        entity.setMovementDate(domain.getMovementDate());

        return entity;
    }
}
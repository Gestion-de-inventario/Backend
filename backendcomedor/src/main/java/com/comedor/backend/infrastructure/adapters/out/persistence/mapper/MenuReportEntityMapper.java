package com.comedor.backend.infrastructure.adapters.out.persistence.mapper;

import com.comedor.backend.domain.model.MenuReport;
import com.comedor.backend.infrastructure.adapters.out.persistence.entity.MenuReportEntity;
import org.springframework.stereotype.Component;
import java.util.stream.Collectors;

@Component
public class MenuReportEntityMapper {

    private final DishMenuEntityMapper dishMenuMapper;
    private final StockMovementEntityMapper stockMovementMapper;
    private final BeneficiaryControlEntityMapper beneficiaryControlMapper;

    public MenuReportEntityMapper(BeneficiaryControlEntityMapper beneficiaryControlEntityMapper, DishMenuEntityMapper dishMenuMapper, StockMovementEntityMapper stockMovementMapper) {
        this.beneficiaryControlMapper = beneficiaryControlEntityMapper;
        this.stockMovementMapper = stockMovementMapper;
        this.dishMenuMapper = dishMenuMapper;
    }

    public MenuReport toDomain(MenuReportEntity entity) {
        if (entity == null) {
            return null;
        }

        MenuReport domain = new MenuReport();
        domain.setId(entity.getId());
        domain.setDate(entity.getDate());
        domain.setCooks(entity.getCooks());

        // Mapeo de la entidad anidada principal
        if (entity.getDishMenu() != null) {
            domain.setDishMenu(dishMenuMapper.toDomain(entity.getDishMenu()));
        }

        domain.setQuantityPrepared(entity.getQuantityPrepared());
        domain.setQuantityRemaining(entity.getQuantityRemaining());
        domain.setTotalEarned(entity.getTotalEarned());
        domain.setTotalSpent(entity.getTotalSpent());
        domain.setStatus(entity.getStatus());

        // Mapeo de listas: Stock y Beneficiarios
        if (entity.getStockMovements() != null) {
            domain.setStockMovements(entity.getStockMovements().stream()
                    .map(stockMovementMapper::toDomain)
                    .collect(Collectors.toList()));
        }

        if (entity.getBeneficiaryControls() != null) {
            domain.setBeneficiaryControls(entity.getBeneficiaryControls().stream()
                    .map(beneficiaryControlMapper::toDomain)
                    .collect(Collectors.toList()));
        }

        return domain;
    }

    public MenuReportEntity toEntity(MenuReport domain) {
        if (domain == null) {
            return null;
        }

        MenuReportEntity entity = new MenuReportEntity();
        entity.setId(domain.getId());
        entity.setDate(domain.getDate());
        entity.setCooks(domain.getCooks());

        if (domain.getDishMenu() != null) {
            entity.setDishMenu(dishMenuMapper.toEntity(domain.getDishMenu()));
        }

        entity.setQuantityPrepared(domain.getQuantityPrepared() != null ? domain.getQuantityPrepared() : 0);
        entity.setQuantityRemaining(domain.getQuantityRemaining() != null ? domain.getQuantityRemaining() : 0);
        entity.setTotalEarned(domain.getTotalEarned());
        entity.setTotalSpent(domain.getTotalSpent());
        entity.setStatus(domain.getStatus());

        // Mapeo de listas hacia la entidad
        // NOTA IMPORTANTE: Al convertir hacia Entity, es vital setear la referencia bidireccional (menuReport)
        // en cada elemento hijo para que JPA/Hibernate guarde correctamente las llaves foráneas.
        if (domain.getStockMovements() != null) {
            entity.setStockMovements(domain.getStockMovements().stream()
                    .map(stockMovement -> {
                        var movementEntity = stockMovementMapper.toEntity(stockMovement);
                        movementEntity.setMenuReport(entity); // Seteo bidireccional
                        return movementEntity;
                    })
                    .collect(Collectors.toList()));
        }

        if (domain.getBeneficiaryControls() != null) {
            entity.setBeneficiaryControls(domain.getBeneficiaryControls().stream()
                    .map(control -> {
                        var controlEntity = beneficiaryControlMapper.toEntity(control);
                        controlEntity.setReport(entity); // Seteo bidireccional
                        return controlEntity;
                    })
                    .collect(Collectors.toList()));
        }

        return entity;
    }
}

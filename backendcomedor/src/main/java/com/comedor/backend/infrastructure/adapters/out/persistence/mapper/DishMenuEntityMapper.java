package com.comedor.backend.infrastructure.adapters.out.persistence.mapper;

import org.springframework.stereotype.Component;

import com.comedor.backend.domain.model.DishMenu;
import com.comedor.backend.domain.model.DishSupply;
import com.comedor.backend.infrastructure.adapters.out.persistence.entity.DishMenuEntity;
import com.comedor.backend.infrastructure.adapters.out.persistence.entity.DishSupplyEntity;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class DishMenuEntityMapper {

    private final ProductEntityMapper productEntityMapper;

    public DishMenu toDomain(DishMenuEntity entity) {

        if (entity == null) {
            return null;
        }

        DishMenu domain = new DishMenu();

        domain.setId(entity.getId());

        domain.setName(entity.getName());

        domain.setStatus(entity.getStatus());

        domain.setSupplies(
                toSupplyDomainList(entity.getSupplies())
        );

        return domain;
    }

    public DishMenuEntity toEntity(DishMenu domain) {

        if (domain == null) {
            return null;
        }

        DishMenuEntity entity = new DishMenuEntity();

        entity.setId(domain.getId());

        entity.setName(domain.getName());

        entity.setStatus(domain.getStatus());

        List<DishSupplyEntity> supplyEntities =
                toSupplyEntityList(domain.getSupplies());

        supplyEntities.forEach(supply ->
                supply.setDishMenu(entity)
        );

        entity.setSupplies(supplyEntities);

        return entity;
    }

    private List<DishSupply> toSupplyDomainList(
            List<DishSupplyEntity> entities
    ) {

        if (entities == null) {
            return new ArrayList<>();
        }

        return entities.stream()
                .map(this::toSupplyDomain)
                .toList();
    }

    private DishSupply toSupplyDomain(
            DishSupplyEntity entity
    ) {

        DishSupply domain = new DishSupply();

        domain.setId(entity.getId());

        domain.setQuantityNeeded(
                entity.getQuantityNeeded()
        );

        domain.setProduct(
                productEntityMapper.toDomain(
                        entity.getProduct()
                )
        );

        return domain;
    }

    private List<DishSupplyEntity> toSupplyEntityList(
            List<DishSupply> domains
    ) {

        if (domains == null) {
            return new ArrayList<>();
        }

        return domains.stream()
                .map(this::toSupplyEntity)
                .toList();
    }

    private DishSupplyEntity toSupplyEntity(
            DishSupply domain
    ) {

        DishSupplyEntity entity =
                new DishSupplyEntity();

        entity.setId(domain.getId());

        entity.setQuantityNeeded(
                domain.getQuantityNeeded()
        );

        entity.setProduct(
                productEntityMapper.toEntity(
                        domain.getProduct()
                )
        );

        return entity;
    }
}
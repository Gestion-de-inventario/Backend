package com.comedor.backend.infrastructure.adapters.out.persistence.mapper;

import com.comedor.backend.domain.model.*;
import com.comedor.backend.infrastructure.adapters.out.persistence.entity.*;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class DonationEntityMapper {
    private final ProductEntityMapper productEntityMapper;

    public DonationEntityMapper(ProductEntityMapper productEntityMapper) {
        this.productEntityMapper = productEntityMapper;
    }

    public Donation toDomain(DonationEntity entity) {

        if (entity == null) {
            return null;
        }

        Donation donation = new Donation();

        donation.setId(entity.getId());

        donation.setDonationDate(entity.getDonationDate());

        donation.setStatus(entity.getStatus());


        donation.setDetails(
                toDetailDomainList(entity.getDetails())
        );

        return donation;
    }

    public DonationEntity toEntity(Donation domain) {

        if (domain == null) {
            return null;
        }

        DonationEntity entity = new DonationEntity();

        entity.setId(domain.getId());

        entity.setDonationDate(domain.getDonationDate());

        entity.setStatus(domain.getStatus());

        List<DonationDetailEntity> detailEntities =
                toDetailEntityList(domain.getDetails());

        detailEntities.forEach(detail ->
                detail.setDonation(entity)
        );

        entity.setDetails(detailEntities);

        return entity;
    }

    private List<DonationDetail> toDetailDomainList(
            List<DonationDetailEntity> entities
    ) {

        if (entities == null) {
            return new ArrayList<>();
        }

        return entities.stream()
                .map(this::toDetailDomain)
                .toList();
    }

    public DonationDetail toDetailDomain(
            DonationDetailEntity entity
    ) {

        DonationDetail detail = new DonationDetail();

        detail.setId(entity.getId());

        detail.setQuantity(entity.getQuantity());

        Product product =
                productEntityMapper.toDomain(entity.getProduct());

        detail.setProduct(product);

        return detail;
    }

    private List<DonationDetailEntity> toDetailEntityList(
            List<DonationDetail> domains
    ) {

        if (domains == null) {
            return new ArrayList<>();
        }

        return domains.stream()
                .map(this::toDetailEntity)
                .toList();
    }

    public  DonationDetailEntity toDetailEntity(
            DonationDetail domain
    ) {

        DonationDetailEntity entity =
                new DonationDetailEntity();

        entity.setId(domain.getId());

        entity.setQuantity(domain.getQuantity());

        ProductEntity productEntity =
                productEntityMapper.toEntity(domain.getProduct());

        entity.setProduct(productEntity);

        return entity;
    }
}

package com.comedor.backend.infrastructure.adapters.out.persistence.mapper;

import com.comedor.backend.domain.model.BeneficiaryControl;
import com.comedor.backend.infrastructure.adapters.out.persistence.entity.BeneficiaryControlEntity;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class BeneficiaryControlEntityMapper {
    private final BeneficiaryEntityMapper beneficiaryEntityMapper;

    public BeneficiaryControlEntityMapper(BeneficiaryEntityMapper beneficiaryEntityMapper) {
        this.beneficiaryEntityMapper = beneficiaryEntityMapper;
    }

    public BeneficiaryControl toDomain(BeneficiaryControlEntity entity)
    {
        BeneficiaryControl domainObject = new BeneficiaryControl();
        domainObject.setId(entity.getId());
        domainObject.setBeneficiario(beneficiaryEntityMapper.convertToDomain(entity.getBeneficiary()));
        domainObject.setReceived(entity.getIsReceived());
        domainObject.setPaid(entity.getIsPaid());
        domainObject.setPayMethod(entity.getPayMethod());
        domainObject.setMenusAmount(entity.getMenusAmount());
        domainObject.setMenuPrice(entity.getMenuPrice());
        return domainObject;
    }

    public BeneficiaryControlEntity toEntity(BeneficiaryControl domainObject)
    {
        BeneficiaryControlEntity entity = new BeneficiaryControlEntity();
        entity.setId(domainObject.getId());
        entity.setBeneficiary(beneficiaryEntityMapper.convertToEntity(domainObject.getBeneficiario()));
        entity.setReceived(domainObject.getReceived());
        entity.setPaid(domainObject.getPaid());
        entity.setPayMethod(domainObject.getPayMethod());
        entity.setMenusAmount(domainObject.getMenusAmount());
        entity.setMenuPrice(domainObject.getMenuPrice());
        return entity;
    }

    public List<BeneficiaryControl> toListDomain(List<BeneficiaryControlEntity> entity)
    {
        return entity.stream().map(this::toDomain).toList();
    }

    public List<BeneficiaryControlEntity> toListEntity(List<BeneficiaryControl> domain)
    {
        return domain.stream().map(this::toEntity).toList();
    }
}

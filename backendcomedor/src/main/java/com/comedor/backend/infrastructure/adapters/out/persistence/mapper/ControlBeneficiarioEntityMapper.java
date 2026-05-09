package com.comedor.backend.infrastructure.adapters.out.persistence.mapper;

import com.comedor.backend.domain.model.ControlBeneficiario;
import com.comedor.backend.infrastructure.adapters.out.persistence.entity.ControlBeneficiarioEntity;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ControlBeneficiarioEntityMapper {
    private final BeneficiarioPersistenceMapper beneficiarioPersistenceMapper;

    public ControlBeneficiarioEntityMapper(BeneficiarioPersistenceMapper beneficiarioPersistenceMapper) {
        this.beneficiarioPersistenceMapper = beneficiarioPersistenceMapper;
    }

    public ControlBeneficiario toDomain(ControlBeneficiarioEntity entity)
    {
        ControlBeneficiario domainObject = new ControlBeneficiario();
        domainObject.setId(entity.getId());
        domainObject.setBeneficiario(beneficiarioPersistenceMapper.convertToDomain(entity.getBeneficiario()));
        domainObject.setReceived(entity.getIsReceived());
        domainObject.setPaid(entity.getIsPaid());
        domainObject.setPayMethod(entity.getPayMethod());
        domainObject.setMenusAmount(entity.getMenusAmount());
        domainObject.setMenuPrice(entity.getMenuPrice());
        return domainObject;
    }

    public ControlBeneficiarioEntity toEntity(ControlBeneficiario domainObject)
    {
        ControlBeneficiarioEntity entity = new ControlBeneficiarioEntity();
        entity.setId(domainObject.getId());
        entity.setBeneficiario(beneficiarioPersistenceMapper.convertToEntity(domainObject.getBeneficiario()));
        entity.setReceived(domainObject.getReceived());
        entity.setPaid(domainObject.getPaid());
        entity.setPayMethod(domainObject.getPayMethod());
        entity.setMenusAmount(domainObject.getMenusAmount());
        entity.setMenuPrice(domainObject.getMenuPrice());
        return entity;
    }

    public List<ControlBeneficiario> toListDomain(List<ControlBeneficiarioEntity> entity)
    {
        return entity.stream().map(this::toDomain).toList();
    }

    public List<ControlBeneficiarioEntity> toListEntity(List<ControlBeneficiario> domain)
    {
        return domain.stream().map(this::toEntity).toList();
    }
}

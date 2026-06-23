package com.comedor.backend.infrastructure.adapters.out.persistence.mapper;

import com.comedor.backend.domain.model.Beneficiary;
import com.comedor.backend.domain.model.BeneficiaryType;
import com.comedor.backend.infrastructure.adapters.out.persistence.entity.BeneficiaryEntity;
import com.comedor.backend.infrastructure.adapters.out.persistence.entity.BeneficiaryTypeEntity;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class BeneficiaryTypeEntityMapper {
    public BeneficiaryTypeEntity convertToEntity(BeneficiaryType beneficiaryType) {
        BeneficiaryTypeEntity beneficiaryTypeEntity = new BeneficiaryTypeEntity();

        if(beneficiaryType.getId()!=null) {
            beneficiaryTypeEntity.setId(beneficiaryType.getId());
        }
        beneficiaryTypeEntity.setName(beneficiaryType.getName());
        beneficiaryTypeEntity.setDescription(beneficiaryType.getDesc());
        beneficiaryTypeEntity.setMenu_cost(beneficiaryType.getMenu_cost());
        beneficiaryTypeEntity.setStatus(beneficiaryType.getStatus());

        return beneficiaryTypeEntity;
    }

    public BeneficiaryType convertToDomain(BeneficiaryTypeEntity entity) {
        return new BeneficiaryType(
                entity.getId(),
                entity.getName(),
                entity.getDescription(),
                entity.getMenu_cost(),
                entity.getStatus()
        );
    }

    public List<BeneficiaryType> convertToListDomain(List<BeneficiaryTypeEntity> entities) {
        return entities.stream()
                .map(this::convertToDomain)
                .toList();
    }
}

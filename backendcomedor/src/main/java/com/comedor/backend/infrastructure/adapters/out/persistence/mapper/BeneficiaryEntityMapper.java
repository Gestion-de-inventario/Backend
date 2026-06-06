package com.comedor.backend.infrastructure.adapters.out.persistence.mapper;

import com.comedor.backend.domain.model.Beneficiary;
import com.comedor.backend.infrastructure.adapters.out.persistence.entity.BeneficiaryEntity;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class BeneficiaryEntityMapper {
    private final BeneficiaryTypeEntityMapper beneficiaryTypeEntityMapper;

    public BeneficiaryEntityMapper(BeneficiaryTypeEntityMapper beneficiaryTypeEntityMapper) {
        this.beneficiaryTypeEntityMapper = beneficiaryTypeEntityMapper;
    }

    public BeneficiaryEntity convertToEntity(Beneficiary beneficiary) {
        BeneficiaryEntity beneficiaryEntity = new BeneficiaryEntity();

        if(beneficiary.getId() > 0) {
            beneficiaryEntity.setId(beneficiary.getId());
        }

        beneficiaryEntity.setDni(beneficiary.getDni());
        beneficiaryEntity.setName(beneficiary.getName());
        beneficiaryEntity.setLastname(beneficiary.getLastname());
        beneficiaryEntity.setStatus(beneficiary.getStatus());
        beneficiaryEntity.setBeneficiaryType(beneficiaryTypeEntityMapper.convertToEntity(beneficiary.getBeneficiaryType()));
        return beneficiaryEntity;
    }

    public Beneficiary convertToDomain(BeneficiaryEntity beneficiaryEntity) {
        return new Beneficiary(
                beneficiaryEntity.getId(),
                beneficiaryEntity.getDni(),
                beneficiaryEntity.getName(),
                beneficiaryEntity.getLastname(),
                beneficiaryEntity.getStatus(),
                beneficiaryTypeEntityMapper.convertToDomain(beneficiaryEntity.getBeneficiaryType())
        );
    }

    public List<Beneficiary> convertToListDomain(List<BeneficiaryEntity> entities) {
        return entities.stream()
                .map(this::convertToDomain)
                .toList();
    }
}

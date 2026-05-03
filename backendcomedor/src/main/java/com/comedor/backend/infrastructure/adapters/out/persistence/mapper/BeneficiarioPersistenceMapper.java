package com.comedor.backend.infrastructure.adapters.out.persistence.mapper;

import com.comedor.backend.domain.model.Beneficiario;
import com.comedor.backend.infrastructure.adapters.out.persistence.entity.BeneficiarioEntity;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class BeneficiarioPersistenceMapper {

    public BeneficiarioEntity convertToEntity(Beneficiario beneficiario) {
        BeneficiarioEntity beneficiarioEntity = new BeneficiarioEntity();

        if(beneficiario.getId() > 0) {
            beneficiarioEntity.setId(beneficiario.getId());
        }

        beneficiarioEntity.setDni(beneficiario.getDni());
        beneficiarioEntity.setName(beneficiario.getName());
        beneficiarioEntity.setLastname(beneficiario.getLastname());
        beneficiarioEntity.setStatus(beneficiario.getStatus());
        return beneficiarioEntity;
    }

    public Beneficiario convertToDomain(BeneficiarioEntity beneficiarioEntity) {
        return new Beneficiario(
                beneficiarioEntity.getId(),
                beneficiarioEntity.getDni(),
                beneficiarioEntity.getName(),
                beneficiarioEntity.getLastname(),
                beneficiarioEntity.getStatus()
        );
    }

    public List<Beneficiario> convertToListDomain(List<BeneficiarioEntity> entities) {
        return entities.stream()
                .map(this::convertToDomain)
                .toList();
    }
}

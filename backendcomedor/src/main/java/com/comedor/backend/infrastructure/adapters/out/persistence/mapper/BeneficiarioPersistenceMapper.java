package com.comedor.backend.infrastructure.adapters.out.persistence.mapper;

import com.comedor.backend.domain.model.Beneficiario;
import com.comedor.backend.infrastructure.adapters.out.persistence.entity.BeneficiarioEntity;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@AllArgsConstructor
public class BeneficiarioPersistenceMapper {
    private final ModelMapper modelMapper;

    public BeneficiarioEntity convertToEntity(Beneficiario beneficiario) {
        return modelMapper.map(beneficiario, BeneficiarioEntity.class);
    }

    public Beneficiario convertToDomain(BeneficiarioEntity entity) {
        return modelMapper.map(entity, Beneficiario.class);
    }

    public List<Beneficiario> convertToListDomain(List<BeneficiarioEntity> entities) {
        return entities.stream()
                .map(this::convertToDomain)
                .toList();
    }
}

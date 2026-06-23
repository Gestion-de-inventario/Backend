package com.comedor.backend.infrastructure.adapters.out.persistence;

import com.comedor.backend.application.ports.out.BeneficiaryTypeRepositoryPort;
import com.comedor.backend.domain.exceptions.BeneficiaryTypeNotFoundException;
import com.comedor.backend.domain.model.BeneficiaryType;
import com.comedor.backend.domain.model.enums.Status;
import com.comedor.backend.infrastructure.adapters.out.persistence.entity.BeneficiaryTypeEntity;
import com.comedor.backend.infrastructure.adapters.out.persistence.mapper.BeneficiaryTypeEntityMapper;
import com.comedor.backend.infrastructure.adapters.out.persistence.repository.BeneficiaryTypeJpaRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
@Component
@AllArgsConstructor
public class BeneficiaryTypeRepositoryAdapter implements BeneficiaryTypeRepositoryPort {

    private final BeneficiaryTypeJpaRepository repository;
    private final BeneficiaryTypeEntityMapper mapper;

    @Override
    public BeneficiaryType save(BeneficiaryType beneficiaryType) {
        BeneficiaryTypeEntity entity = mapper.convertToEntity(beneficiaryType);

        BeneficiaryTypeEntity savedEntity = repository.save(entity);

        return mapper.convertToDomain(savedEntity);
    }

    @Override
    public boolean existsByName(String name) {
        return repository.existsByName(name);
    }

    @Override
    public boolean existsByNameAndIdNot(String name, Integer id) {
        return repository.existsByNameAndIdNot(name, id);
    }

    @Override
    public BeneficiaryType findById(Integer id) {
        return mapper.convertToDomain(repository.findById(id).
                orElseThrow(()-> new BeneficiaryTypeNotFoundException("Tipo de beneficiario no encontrado")));
    }

    @Override
    public List<BeneficiaryType> findByStatus(Status status) {
        if(status==null)
        {
            return mapper.convertToListDomain(repository.findAll());
        }

        return mapper.convertToListDomain(repository.findByStatus(status));

    }

    @Override
    public BeneficiaryType update(BeneficiaryType beneficiaryType) {
        return save(beneficiaryType);
    }
}

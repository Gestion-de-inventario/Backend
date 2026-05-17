package com.comedor.backend.infrastructure.adapters.out.persistence;

import com.comedor.backend.application.ports.out.BeneficiaryRepositoryPort;
import com.comedor.backend.domain.model.Beneficiary;
import com.comedor.backend.domain.model.enums.Estado;
import com.comedor.backend.infrastructure.adapters.out.persistence.entity.BeneficiaryEntity;
import com.comedor.backend.infrastructure.adapters.out.persistence.mapper.BeneficiaryEntityMapper;
import com.comedor.backend.infrastructure.adapters.out.persistence.repository.BeneficiaryJpaRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
@AllArgsConstructor
public class BeneficiaryRepositoryAdapter implements BeneficiaryRepositoryPort {

    private final BeneficiaryJpaRepository jpaRepository;
    private final BeneficiaryEntityMapper persistenceMapper;


    @Override
    public Beneficiary guardar(Beneficiary beneficiary) {

        BeneficiaryEntity entity = persistenceMapper.convertToEntity(beneficiary);

        BeneficiaryEntity savedEntity = jpaRepository.save(entity);

        return persistenceMapper.convertToDomain(savedEntity);
    }

    @Override
    public boolean existePorDni(String dni) {
        return jpaRepository.existsByDni(dni);
    }

    @Override
    public Optional<Beneficiary> buscarPorDni(String dni) {

        Optional<BeneficiaryEntity> entityOptional = jpaRepository.findByDni(dni);

        if(entityOptional.isPresent()) {
            return Optional.of(persistenceMapper.convertToDomain(entityOptional.get()));
        }
        return Optional.empty();
    }

    @Override
    public Optional<Beneficiary> findById(Integer id) {
        return jpaRepository.findById(id)
                .map(persistenceMapper::convertToDomain);
    }

    @Override
    public List<Beneficiary> getBeneficiarioByStatus(Estado estado) {
        if(estado == null)
        {   return persistenceMapper.convertToListDomain(jpaRepository.findAll());
        } else if (estado == Estado.ACTIVO)
        {
            return persistenceMapper.convertToListDomain(jpaRepository.getAllBeneficiariosActivos());
        }
        else if (estado == Estado.INACTIVO)
        {
            return persistenceMapper.convertToListDomain(jpaRepository.getAllBeneficiariosInactivos());
        }
        return null;
    }


}

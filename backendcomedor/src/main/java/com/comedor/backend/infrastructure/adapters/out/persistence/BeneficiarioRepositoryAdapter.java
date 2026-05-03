package com.comedor.backend.infrastructure.adapters.out.persistence;

import com.comedor.backend.application.ports.out.BeneficiarioRepositoryPort;
import com.comedor.backend.domain.model.Beneficiario;
import com.comedor.backend.infrastructure.adapters.out.persistence.entity.BeneficiarioEntity;
import com.comedor.backend.infrastructure.adapters.out.persistence.mapper.BeneficiarioPersistenceMapper;
import com.comedor.backend.infrastructure.adapters.out.persistence.repository.BeneficiarioJpaRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class BeneficiarioRepositoryAdapter implements BeneficiarioRepositoryPort {

    private final BeneficiarioJpaRepository jpaRepository;
    private final BeneficiarioPersistenceMapper persistenceMapper;

    @Override
    public Beneficiario guardar(Beneficiario beneficiario) {

        BeneficiarioEntity entity = persistenceMapper.convertToEntity(beneficiario);

        BeneficiarioEntity savedEntity = jpaRepository.save(entity);

        return persistenceMapper.convertToDomain(savedEntity);
    }

    @Override
    public boolean existePorDni(String dni) {
        return jpaRepository.existsByDni(dni);
    }


}

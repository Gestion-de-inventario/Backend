package com.comedor.backend.infrastructure.adapters.out.persistence;

import com.comedor.backend.application.ports.out.BeneficiaryRepositoryPort;
import com.comedor.backend.domain.exceptions.BeneficiarioNoEncontradoException;
import com.comedor.backend.domain.model.Beneficiary;
import com.comedor.backend.domain.model.BeneficiaryType;
import com.comedor.backend.domain.model.enums.Estado;
import com.comedor.backend.infrastructure.adapters.out.persistence.entity.BeneficiaryEntity;
import com.comedor.backend.infrastructure.adapters.out.persistence.entity.BeneficiaryTypeEntity;
import com.comedor.backend.infrastructure.adapters.out.persistence.entity.CategoryEntity;
import com.comedor.backend.infrastructure.adapters.out.persistence.mapper.BeneficiaryEntityMapper;
import com.comedor.backend.infrastructure.adapters.out.persistence.repository.BeneficiaryJpaRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
@AllArgsConstructor
public class BeneficiaryRepositoryAdapter implements BeneficiaryRepositoryPort {

    private final BeneficiaryJpaRepository jpaRepository;
    private final BeneficiaryEntityMapper persistenceMapper;
    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public Beneficiary guardar(Beneficiary beneficiary) {

        BeneficiaryEntity entity = persistenceMapper.convertToEntity(beneficiary);

        entity.setBeneficiaryType(
                entityManager.getReference(
                        BeneficiaryTypeEntity.class,
                        beneficiary.getBeneficiaryType().getId()
                )
        );

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

    @Override
    public Beneficiary activar(Integer id) {
        BeneficiaryEntity entity = jpaRepository.findById(id)
                .orElseThrow(() -> new BeneficiarioNoEncontradoException("Beneficiario no encontrado: " + id));
        entity.setStatus(Estado.ACTIVO);
        return persistenceMapper.convertToDomain(jpaRepository.save(entity));
    }

    @Override
    public Beneficiary desactivar(Integer id) {
        BeneficiaryEntity entity = jpaRepository.findById(id)
                .orElseThrow(() -> new BeneficiarioNoEncontradoException("Beneficiario no encontrado: " + id));
        entity.setStatus(Estado.INACTIVO);
        return persistenceMapper.convertToDomain(jpaRepository.save(entity));
    }

    @Override
    public boolean CategoryisItAssignedToBeneficiary(Integer id) {

        return jpaRepository
                .existsByBeneficiaryTypeIdAndStatus(
                        id,
                        Estado.ACTIVO
                );
    }

}

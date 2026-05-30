package com.comedor.backend.infrastructure.adapters.out.persistence;

import com.comedor.backend.application.ports.out.BeneficiaryControlRepositoryPort;
import com.comedor.backend.domain.exceptions.ControlBeneficiarioNoEncontradoException;
import com.comedor.backend.domain.model.BeneficiaryControl;
import com.comedor.backend.infrastructure.adapters.out.persistence.entity.BeneficiaryEntity;
import com.comedor.backend.infrastructure.adapters.out.persistence.entity.BeneficiaryControlEntity;
import com.comedor.backend.infrastructure.adapters.out.persistence.entity.MenuReportEntity;
import com.comedor.backend.infrastructure.adapters.out.persistence.mapper.BeneficiaryControlEntityMapper;
import com.comedor.backend.infrastructure.adapters.out.persistence.repository.BeneficiaryControlJpaRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
@Component
@RequiredArgsConstructor
public class BeneficiaryControlRepositoryAdapter implements BeneficiaryControlRepositoryPort {

    private final BeneficiaryControlJpaRepository beneficiaryControlJpaRepository;

    private final BeneficiaryControlEntityMapper beneficiaryControlEntityMapper;

    @PersistenceContext private EntityManager entityManager;

    @Override
    public BeneficiaryControl agregarBeneficiario(
            int reporteId,
            BeneficiaryControl control
    ) {

        BeneficiaryControlEntity entity =
                beneficiaryControlEntityMapper
                        .toEntity(control);

        entity.setBeneficiary(
                entityManager.getReference(
                        BeneficiaryEntity.class,
                        control.getBeneficiario().getId()
                )
        );

        entity.setReport(
                entityManager.getReference(
                        MenuReportEntity.class,
                        reporteId
                )
        );

        BeneficiaryControlEntity saved =
                beneficiaryControlJpaRepository
                        .save(entity);

        return beneficiaryControlEntityMapper
                .toDomain(saved);
    }

    @Override
    public BeneficiaryControl actualizarBeneficiario(int reporteId, int controlId, BeneficiaryControl control)
    {
        BeneficiaryControlEntity entity =
                beneficiaryControlJpaRepository
                        .findById(controlId)
                        .orElseThrow(
                                () -> new ControlBeneficiarioNoEncontradoException(
                                        "Control no encontrado"
                                )
                        );

        if(entity.getReport().getId() != reporteId)
        {
            throw new IllegalArgumentException(
                    "El registro no pertenece al reporte"
            );
        }

        entity.setPaid(control.getPaid());

        entity.setReceived(control.getReceived());

        entity.setPayMethod(control.getPayMethod());

        entity.setMenusAmount(control.getMenusAmount());

        entity.setMenuPrice(control.getMenuPrice());

        BeneficiaryControlEntity updated =
                beneficiaryControlJpaRepository
                        .save(entity);

        return beneficiaryControlEntityMapper
                .toDomain(updated);
    }

    @Override
    public void eliminarBeneficiario(int reporteId,int controlId) {
        BeneficiaryControlEntity entity = beneficiaryControlJpaRepository.findById(controlId)
                .orElseThrow(()-> new ControlBeneficiarioNoEncontradoException("Control no encontrado")) ;

        if(entity.getReport().getId() != reporteId)
        {
            throw new IllegalArgumentException(
                    "El registro no pertenece al reporte"
            );
        }
        beneficiaryControlJpaRepository.deleteById(controlId);
    }

    @Override
    public BeneficiaryControl findById(int controlId) {

        return beneficiaryControlEntityMapper.toDomain(

                beneficiaryControlJpaRepository
                        .findById(controlId)
                        .orElseThrow(
                                () -> new ControlBeneficiarioNoEncontradoException(
                                        "Control no encontrado"
                                )
                        )
        );
    }

    @Override
    public List<BeneficiaryControl> findByReporteId(
            int reporteId
    ) {

        return beneficiaryControlEntityMapper
                .toListDomain(

                        beneficiaryControlJpaRepository
                                .findByReportId(reporteId)
                );
    }
}

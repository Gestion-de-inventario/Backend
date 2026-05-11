package com.comedor.backend.infrastructure.adapters.out.persistence;

import com.comedor.backend.application.ports.out.ControlBeneficiarioRepositoryPort;
import com.comedor.backend.domain.exceptions.ControlBeneficiarioNoEncontradoException;
import com.comedor.backend.domain.model.ControlBeneficiario;
import com.comedor.backend.infrastructure.adapters.out.persistence.entity.BeneficiarioEntity;
import com.comedor.backend.infrastructure.adapters.out.persistence.entity.ControlBeneficiarioEntity;
import com.comedor.backend.infrastructure.adapters.out.persistence.entity.ReporteMenuEntity;
import com.comedor.backend.infrastructure.adapters.out.persistence.mapper.ControlBeneficiarioEntityMapper;
import com.comedor.backend.infrastructure.adapters.out.persistence.repository.ControlBeneficiarioJpaRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
@Component
@RequiredArgsConstructor
public class ControlBeneficiarioRepositoryAdapter implements ControlBeneficiarioRepositoryPort {

    private final ControlBeneficiarioJpaRepository controlBeneficiarioJpaRepository;

    private final ControlBeneficiarioEntityMapper controlBeneficiarioEntityMapper;

    @PersistenceContext private EntityManager entityManager;

    @Override
    public ControlBeneficiario agregarBeneficiario(
            int reporteId,
            ControlBeneficiario control
    ) {

        ControlBeneficiarioEntity entity =
                controlBeneficiarioEntityMapper
                        .toEntity(control);

        entity.setBeneficiario(
                entityManager.getReference(
                        BeneficiarioEntity.class,
                        control.getBeneficiario().getId()
                )
        );

        entity.setReporte(
                entityManager.getReference(
                        ReporteMenuEntity.class,
                        reporteId
                )
        );

        ControlBeneficiarioEntity saved =
                controlBeneficiarioJpaRepository
                        .save(entity);

        return controlBeneficiarioEntityMapper
                .toDomain(saved);
    }

    @Override
    public ControlBeneficiario actualizarBeneficiario(int reporteId,int controlId,ControlBeneficiario control)
    {
        ControlBeneficiarioEntity entity =
                controlBeneficiarioJpaRepository
                        .findById(controlId)
                        .orElseThrow(
                                () -> new ControlBeneficiarioNoEncontradoException(
                                        "Control no encontrado"
                                )
                        );

        if(entity.getReporte().getId() != reporteId)
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

        ControlBeneficiarioEntity updated =
                controlBeneficiarioJpaRepository
                        .save(entity);

        return controlBeneficiarioEntityMapper
                .toDomain(updated);
    }

    @Override
    public void eliminarBeneficiario(int reporteId,int controlId) {
        ControlBeneficiarioEntity entity = controlBeneficiarioJpaRepository.findById(controlId)
                .orElseThrow(()-> new ControlBeneficiarioNoEncontradoException("Control no encontrado")) ;

        if(entity.getReporte().getId() != reporteId)
        {
            throw new IllegalArgumentException(
                    "El registro no pertenece al reporte"
            );
        }
        controlBeneficiarioJpaRepository.deleteById(controlId);
    }

    @Override
    public ControlBeneficiario findById(int controlId) {

        return controlBeneficiarioEntityMapper.toDomain(

                controlBeneficiarioJpaRepository
                        .findById(controlId)
                        .orElseThrow(
                                () -> new ControlBeneficiarioNoEncontradoException(
                                        "Control no encontrado"
                                )
                        )
        );
    }

    @Override
    public List<ControlBeneficiario> findByReporteId(
            int reporteId
    ) {

        return controlBeneficiarioEntityMapper
                .toListDomain(

                        controlBeneficiarioJpaRepository
                                .findByReporteId(reporteId)
                );
    }
}

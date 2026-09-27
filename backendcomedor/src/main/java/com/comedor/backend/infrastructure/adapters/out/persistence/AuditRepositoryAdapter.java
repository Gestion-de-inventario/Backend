package com.comedor.backend.infrastructure.adapters.out.persistence;

import com.comedor.backend.application.ports.out.AuditRepositoryPort;
import com.comedor.backend.domain.model.Audit;
import com.comedor.backend.domain.model.enums.AuditAction;
import com.comedor.backend.infrastructure.adapters.out.persistence.entity.AuditEntity;
import com.comedor.backend.infrastructure.adapters.out.persistence.entity.DonationEntity;
import com.comedor.backend.infrastructure.adapters.out.persistence.entity.UserEntity;
import com.comedor.backend.infrastructure.adapters.out.persistence.mapper.AuditEntityMapper;
import com.comedor.backend.infrastructure.adapters.out.persistence.repository.AuditJpaRepository;
import com.comedor.backend.infrastructure.adapters.out.persistence.repository.UserJpaRepository;
import com.comedor.backend.infrastructure.adapters.out.persistence.repository.specification.AuditSpecification;
import com.comedor.backend.infrastructure.adapters.out.persistence.repository.specification.DonationSpecification;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

@Component
@RequiredArgsConstructor
public class AuditRepositoryAdapter  implements AuditRepositoryPort {

    private final AuditJpaRepository auditJpaRepository;
    private final AuditEntityMapper auditEntityMapper;
    private final UserJpaRepository userJpaRepository;

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public void registrar(Audit audit) {

        UserEntity userEntity = userJpaRepository
                .findByUsername(audit.getUser().getUsername())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        AuditEntity entity = auditEntityMapper.toEntity(audit);

        entity.setUser(userEntity);

        auditJpaRepository.save(entity);
    }

    @Override
    public Page<Audit> list(Pageable pageable) {

        return auditJpaRepository
                .findAll(pageable)
                .map(auditEntityMapper::toDomain);
    }

    @Override
    public Page<Audit> showAudits(
            LocalDate startDate,
            LocalDate endDate,
            AuditAction action,
            Pageable pageable) {
        if (
                startDate != null &&
                        endDate != null &&
                        startDate.isAfter(endDate)
        ) {
            throw new IllegalArgumentException(
                    "La fecha de inicio no puede ser mayor que la fecha fin"
            );
        }
        Specification<AuditEntity> spec =
                (root, query, cb) -> cb.conjunction();

        if(startDate != null){
            spec = spec.and(
                    AuditSpecification.dateTimeAfter(startDate)
            );
        }

        if(endDate != null){
            spec = spec.and(
                    AuditSpecification.dateTimeBefore(endDate)
            );
        }

        if(action != null){
            spec = spec.and(
                    AuditSpecification.hasAction(action)
            );
        }

        return auditJpaRepository.findAll(spec,pageable).map(auditEntityMapper::toDomain);
    }

    @Override
    public List<Audit> listByPeriod(
            String fechaInicio,
            String fechaFin) {

        return auditJpaRepository
                .findByPeriodList(fechaInicio, fechaFin)
                .stream()
                .map(auditEntityMapper::toDomain)
                .toList();
    }

}

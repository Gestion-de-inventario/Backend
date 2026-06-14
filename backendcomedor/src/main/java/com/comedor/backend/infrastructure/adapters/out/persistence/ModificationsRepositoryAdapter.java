package com.comedor.backend.infrastructure.adapters.out.persistence;

import com.comedor.backend.application.ports.out.ModificationsRepositoryPort;
import com.comedor.backend.domain.model.Modifications;
import com.comedor.backend.infrastructure.adapters.out.persistence.entity.ModificationsEntity;
import com.comedor.backend.infrastructure.adapters.out.persistence.entity.UserEntity;
import com.comedor.backend.infrastructure.adapters.out.persistence.mapper.ModificationsEntityMapper;
import com.comedor.backend.infrastructure.adapters.out.persistence.repository.ModificationsJpaRepository;
import com.comedor.backend.infrastructure.adapters.out.persistence.repository.UserJpaRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class ModificationsRepositoryAdapter implements ModificationsRepositoryPort {

    private final ModificationsJpaRepository modificationsJpaRepository;
    private final ModificationsEntityMapper modificationsEntityMapper;
    private final UserJpaRepository userJpaRepository;

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public void registrar(Modifications modifications) {

        ModificationsEntity entity = new ModificationsEntity();

        UserEntity userEntity = userJpaRepository.findByUsername(modifications.getUser().getUsername())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        entity.setUser(userEntity);
        entity.setEditedClass(modifications.getEditedClass());
        entity.setEditedAttribute(modifications.getEditedAttribute());
        entity.setPreviousValue(modifications.getPreviousValue());
        entity.setNewValue(modifications.getNewValue());
        entity.setDateTime(modifications.getDateTime());

        modificationsJpaRepository.save(entity);
    }

    @Override
    public Page<Modifications> list(Pageable pageable) {
        return modificationsJpaRepository
                .findAll(pageable)
                .map(modificationsEntityMapper::toDomain);
    }

    @Override
    public Page<Modifications> listByPeriod(String fechaInicio, String fechaFin, Pageable pageable) {
        String baseQuery = "SELECT m FROM ModificationsEntity m WHERE 1=1";
        String countQuery = "SELECT COUNT(m) FROM ModificationsEntity m WHERE 1=1";

        if (fechaInicio != null) {
            baseQuery += " AND m.dateTime >= :fechaInicio";
            countQuery += " AND m.dateTime >= :fechaInicio";
        }
        if (fechaFin != null) {
            baseQuery += " AND m.dateTime <= :fechaFin";
            countQuery += " AND m.dateTime <= :fechaFin";
        }
        baseQuery += " ORDER BY m.id DESC";

        TypedQuery<ModificationsEntity> query = entityManager.createQuery(baseQuery, ModificationsEntity.class);
        TypedQuery<Long> count = entityManager.createQuery(countQuery, Long.class);

        if (fechaInicio != null) {
            LocalDateTime inicio = LocalDate.parse(fechaInicio).atStartOfDay();
            query.setParameter("fechaInicio", inicio);
            count.setParameter("fechaInicio", inicio);
        }
        if (fechaFin != null) {
            LocalDateTime fin = LocalDate.parse(fechaFin).atTime(23, 59, 59);
            query.setParameter("fechaFin", fin);
            count.setParameter("fechaFin", fin);
        }

        int pageNumber = pageable.getPageNumber();
        int pageSize = pageable.getPageSize();
        query.setFirstResult(pageNumber * pageSize);
        query.setMaxResults(pageSize);

        List<Modifications> results = query.getResultList()
                .stream().map(modificationsEntityMapper::toDomain).toList();
        long total = count.getSingleResult();

        return new org.springframework.data.domain.PageImpl<>(results, pageable, total);
    }

    @Override
    public List<Modifications> listByPeriod(String fechaInicio, String fechaFin) {
        String baseQuery = "SELECT m FROM ModificationsEntity m WHERE 1=1";

        if (fechaInicio != null) baseQuery += " AND m.dateTime >= :fechaInicio";
        if (fechaFin != null) baseQuery += " AND m.dateTime <= :fechaFin";
        baseQuery += " ORDER BY m.id DESC";

        TypedQuery<ModificationsEntity> query = entityManager.createQuery(baseQuery, ModificationsEntity.class);

        if (fechaInicio != null) query.setParameter("fechaInicio", LocalDate.parse(fechaInicio).atStartOfDay());
        if (fechaFin != null) query.setParameter("fechaFin", LocalDate.parse(fechaFin).atTime(23, 59, 59));

        return query.getResultList().stream().map(modificationsEntityMapper::toDomain).toList();
    }

}

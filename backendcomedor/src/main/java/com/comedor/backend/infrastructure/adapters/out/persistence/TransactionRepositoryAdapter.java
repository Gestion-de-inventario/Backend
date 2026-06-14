package com.comedor.backend.infrastructure.adapters.out.persistence;

import com.comedor.backend.application.ports.out.TransactionRepositoryPort;
import com.comedor.backend.domain.model.Transactions;
import com.comedor.backend.infrastructure.adapters.out.persistence.entity.ProductEntity;
import com.comedor.backend.infrastructure.adapters.out.persistence.entity.TransactionsEntity;
import com.comedor.backend.infrastructure.adapters.out.persistence.entity.UserEntity;
import com.comedor.backend.infrastructure.adapters.out.persistence.mapper.TransactionEntityMapper;
import com.comedor.backend.infrastructure.adapters.out.persistence.repository.TransactionJpaRepository;
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
@Component
@RequiredArgsConstructor
public class TransactionRepositoryAdapter implements TransactionRepositoryPort {
    private final TransactionJpaRepository transactionJpaRepository;
    private final TransactionEntityMapper transactionEntityMapper;
    @PersistenceContext
    private EntityManager entityManager;
    @Override
    public Transactions createTransaccion(Transactions transaccion) {
        TransactionsEntity transaccionEntity = transactionEntityMapper.toEntity(transaccion);
        transaccionEntity.setProduct(entityManager.getReference(ProductEntity.class,transaccion.getProduct().getId()));
        transaccionEntity.setUser(entityManager.getReference(UserEntity.class,transaccion.getUser().getId()));
        return transactionEntityMapper.toDomain(transactionJpaRepository.save(transaccionEntity));
    }

    @Override
    public Transactions showTransaccionById(int id) {
        return null;
    }

    @Override
    public Page<Transactions> showTransacciones(Pageable pageable) {
        return transactionJpaRepository.findAll(pageable)
                .map(transactionEntityMapper::toDomain);
    }

    @Override
    public Page<Transactions> showTransaccionesByPeriod(String fechaInicio, String fechaFin, Pageable pageable) {
        String baseQuery = "SELECT t FROM TransactionsEntity t WHERE 1=1";
        String countQuery = "SELECT COUNT(t) FROM TransactionsEntity t WHERE 1=1";

        if (fechaInicio != null) {
            baseQuery += " AND t.dateTime >= :fechaInicio";
            countQuery += " AND t.dateTime >= :fechaInicio";
        }
        if (fechaFin != null) {
            baseQuery += " AND t.dateTime <= :fechaFin";
            countQuery += " AND t.dateTime <= :fechaFin";
        }
        baseQuery += " ORDER BY t.id DESC";

        TypedQuery<TransactionsEntity> query = entityManager.createQuery(baseQuery, TransactionsEntity.class);
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

        List<Transactions> results = query.getResultList()
                .stream().map(transactionEntityMapper::toDomain).toList();
        long total = count.getSingleResult();

        return new org.springframework.data.domain.PageImpl<>(results, pageable, total);
    }

    @Override
    public List<Transactions> showTransaccionesByPeriod(String fechaInicio, String fechaFin) {
        String baseQuery = "SELECT t FROM TransactionsEntity t WHERE 1=1";

        if (fechaInicio != null) baseQuery += " AND t.dateTime >= :fechaInicio";
        if (fechaFin != null) baseQuery += " AND t.dateTime <= :fechaFin";
        baseQuery += " ORDER BY t.id DESC";

        TypedQuery<TransactionsEntity> query = entityManager.createQuery(baseQuery, TransactionsEntity.class);

        if (fechaInicio != null) query.setParameter("fechaInicio", LocalDate.parse(fechaInicio).atStartOfDay());
        if (fechaFin != null) query.setParameter("fechaFin", LocalDate.parse(fechaFin).atTime(23, 59, 59));

        return query.getResultList().stream().map(transactionEntityMapper::toDomain).toList();
    }
}

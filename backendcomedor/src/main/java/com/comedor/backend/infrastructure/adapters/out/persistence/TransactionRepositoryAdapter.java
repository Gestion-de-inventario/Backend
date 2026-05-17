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
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
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
    public List<Transactions> showTransacciones() {
        return transactionEntityMapper.toListDomain(transactionJpaRepository.findAll());
    }

    @Override
    public Transactions showTransaccionById(int id) {
        return null;
    }

    @Override
    public List<Transactions> showTransaccionesByUserId(Integer id) {
        return List.of();
    }

    @Override
    public List<Transactions> showTransaccionesByPeriod(LocalDate fechaInicio, LocalDate fechaFin) {
        return List.of();
    }
}

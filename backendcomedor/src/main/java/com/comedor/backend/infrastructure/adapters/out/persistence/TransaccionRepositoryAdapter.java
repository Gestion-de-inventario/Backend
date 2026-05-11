package com.comedor.backend.infrastructure.adapters.out.persistence;

import com.comedor.backend.application.ports.out.TransaccionRepositoryPort;
import com.comedor.backend.domain.model.Transacciones;
import com.comedor.backend.infrastructure.adapters.out.persistence.entity.ProductoEntity;
import com.comedor.backend.infrastructure.adapters.out.persistence.entity.TransaccionesEntity;
import com.comedor.backend.infrastructure.adapters.out.persistence.entity.UsuarioEntity;
import com.comedor.backend.infrastructure.adapters.out.persistence.mapper.TransaccionEntityMapper;
import com.comedor.backend.infrastructure.adapters.out.persistence.repository.TransaccionJpaRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;
@Component
@RequiredArgsConstructor
public class TransaccionRepositoryAdapter implements TransaccionRepositoryPort {
    private final TransaccionJpaRepository transaccionJpaRepository;
    private final TransaccionEntityMapper transaccionEntityMapper;
    @PersistenceContext
    private EntityManager entityManager;
    @Override
    public Transacciones createTransaccion(Transacciones transaccion) {
        TransaccionesEntity transaccionEntity = transaccionEntityMapper.toEntity(transaccion);
        transaccionEntity.setProduct(entityManager.getReference(ProductoEntity.class,transaccion.getProduct().getId()));
        transaccionEntity.setUser(entityManager.getReference(UsuarioEntity.class,transaccion.getUser().getId()));
        return transaccionEntityMapper.toDomain(transaccionJpaRepository.save(transaccionEntity));
    }

    @Override
    public List<Transacciones> showTransacciones() {
        return transaccionEntityMapper.toListDomain(transaccionJpaRepository.findAll());
    }

    @Override
    public Transacciones showTransaccionById(int id) {
        return null;
    }

    @Override
    public List<Transacciones> showTransaccionesByUserId(Integer id) {
        return List.of();
    }

    @Override
    public List<Transacciones> showTransaccionesByPeriod(LocalDate fechaInicio, LocalDate fechaFin) {
        return List.of();
    }
}

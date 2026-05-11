package com.comedor.backend.infrastructure.adapters.out.persistence;

import com.comedor.backend.application.ports.out.RegistroProductoRepositoryPort;
import com.comedor.backend.domain.exceptions.RegistroNoEncontradoException;
import com.comedor.backend.domain.model.Registro;
import com.comedor.backend.infrastructure.adapters.out.persistence.entity.ProductoEntity;
import com.comedor.backend.infrastructure.adapters.out.persistence.entity.RegistroEntity;
import com.comedor.backend.infrastructure.adapters.out.persistence.entity.ReporteMenuEntity;
import com.comedor.backend.infrastructure.adapters.out.persistence.mapper.ProductoEntityMapper;
import com.comedor.backend.infrastructure.adapters.out.persistence.mapper.RegistroEntityMapper;
import com.comedor.backend.infrastructure.adapters.out.persistence.repository.RegistroProductoJpaRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class RegistroProductoRepositoryAdapter implements RegistroProductoRepositoryPort {
    private final RegistroProductoJpaRepository registroProductoJpaRepository;
    private final RegistroEntityMapper registroEntityMapper;

    @PersistenceContext
    private EntityManager entityManager;
    @Override
    public Registro agregarRegistroProducto(int reporteId, Registro registro)
    {   RegistroEntity entity = registroEntityMapper.toEntity(registro);
        entity.setProduct(
                entityManager.getReference(
                        ProductoEntity.class,
                        registro.getProduct().getId()
                )
        );
        entity.setReporte(
                entityManager.getReference(
                        ReporteMenuEntity.class,
                        reporteId
                )
        );
        RegistroEntity saved = registroProductoJpaRepository.save(entity);
        return registroEntityMapper.toDomain(saved);
    }
    @Override
    public Registro actualizarRegistroProducto(int reporteId,int registroId,Registro registro)
    {
        RegistroEntity entity = registroProductoJpaRepository
                .findById(registroId)
                .orElseThrow(() -> new RegistroNoEncontradoException("Registro no encontrado"));

        if(entity.getReporte().getId() != reporteId){
            throw new IllegalArgumentException(
                    "El registro no pertenece al reporte"
            );
        }

        if(registro.getAmount() != null)
            entity.setAmount(registro.getAmount());

        if(registro.getUnitPrice() != null)
            entity.setUnitPrice(registro.getUnitPrice());

        if(registro.getProduct() != null){
            entity.setProduct(
                    entityManager.getReference(
                            ProductoEntity.class,
                            registro.getProduct().getId()
                    )
            );
        }

        RegistroEntity updated =
                registroProductoJpaRepository.save(entity);

        return registroEntityMapper.toDomain(updated);
    }
    @Override
    public void eliminarRegistroProducto(int reporteId,int registroId) {
        RegistroEntity entity = registroProductoJpaRepository
                .findById(registroId)
                .orElseThrow(() ->
                        new RegistroNoEncontradoException("Registro no encontrado"));

        if(entity.getReporte().getId() != reporteId){
            throw new IllegalArgumentException(
                    "El registro no pertenece al reporte"
            );
        }

        registroProductoJpaRepository.delete(entity);
    }

    @Override
    public List<Registro> findByReporteId(int reporteId) {
        List<RegistroEntity> entities =registroProductoJpaRepository.findByReporteId(reporteId);
        return registroEntityMapper.toListDomain(entities);
    }

    @Override
    public Registro findById(int id) {
        return registroEntityMapper.toDomain(
                registroProductoJpaRepository.findById(id)
                        .orElseThrow(
                                () -> new RegistroNoEncontradoException(
                                        "Registro no encontrado"
                                )
                        )
        );
    }
}

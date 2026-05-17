package com.comedor.backend.infrastructure.adapters.out.persistence;

import com.comedor.backend.application.ports.out.ProductRecordRepositoryPort;
import com.comedor.backend.domain.exceptions.RegistroNoEncontradoException;
import com.comedor.backend.domain.model.Record;
import com.comedor.backend.infrastructure.adapters.out.persistence.entity.ProductEntity;
import com.comedor.backend.infrastructure.adapters.out.persistence.entity.RecordEntity;
import com.comedor.backend.infrastructure.adapters.out.persistence.entity.MenuReportEntity;
import com.comedor.backend.infrastructure.adapters.out.persistence.mapper.RecordEntityMapper;
import com.comedor.backend.infrastructure.adapters.out.persistence.repository.ProductRecordJpaRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class ProductRecordRepositoryAdapter implements ProductRecordRepositoryPort {
    private final ProductRecordJpaRepository productRecordJpaRepository;
    private final RecordEntityMapper recordEntityMapper;

    @PersistenceContext
    private EntityManager entityManager;
    @Override
    public Record agregarRegistroProducto(int reporteId, Record record)
    {   RecordEntity entity = recordEntityMapper.toEntity(record);
        entity.setProduct(
                entityManager.getReference(
                        ProductEntity.class,
                        record.getProduct().getId()
                )
        );
        entity.setReport(
                entityManager.getReference(
                        MenuReportEntity.class,
                        reporteId
                )
        );
        RecordEntity saved = productRecordJpaRepository.save(entity);
        return recordEntityMapper.toDomain(saved);
    }
    @Override
    public Record actualizarRegistroProducto(int reporteId, int registroId, Record record)
    {
        RecordEntity entity = productRecordJpaRepository
                .findById(registroId)
                .orElseThrow(() -> new RegistroNoEncontradoException("Registro no encontrado"));

        if(entity.getReport().getId() != reporteId){
            throw new IllegalArgumentException(
                    "El registro no pertenece al reporte"
            );
        }

        if(record.getAmount() != null)
            entity.setAmount(record.getAmount());

        if(record.getUnitPrice() != null)
            entity.setUnitPrice(record.getUnitPrice());

        if(record.getProduct() != null){
            entity.setProduct(
                    entityManager.getReference(
                            ProductEntity.class,
                            record.getProduct().getId()
                    )
            );
        }

        RecordEntity updated =
                productRecordJpaRepository.save(entity);

        return recordEntityMapper.toDomain(updated);
    }
    @Override
    public void eliminarRegistroProducto(int reporteId,int registroId) {
        RecordEntity entity = productRecordJpaRepository
                .findById(registroId)
                .orElseThrow(() ->
                        new RegistroNoEncontradoException("Registro no encontrado"));

        if(entity.getReport().getId() != reporteId){
            throw new IllegalArgumentException(
                    "El registro no pertenece al reporte"
            );
        }

        productRecordJpaRepository.delete(entity);
    }

    @Override
    public List<Record> findByReporteId(int reporteId) {
        List<RecordEntity> entities = productRecordJpaRepository.findByReportId(reporteId);
        return recordEntityMapper.toListDomain(entities);
    }

    @Override
    public Record findById(int id) {
        return recordEntityMapper.toDomain(
                productRecordJpaRepository.findById(id)
                        .orElseThrow(
                                () -> new RegistroNoEncontradoException(
                                        "Registro no encontrado"
                                )
                        )
        );
    }
}

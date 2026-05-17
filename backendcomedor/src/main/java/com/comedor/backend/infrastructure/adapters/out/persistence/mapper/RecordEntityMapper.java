package com.comedor.backend.infrastructure.adapters.out.persistence.mapper;

import com.comedor.backend.domain.model.Record;
import com.comedor.backend.infrastructure.adapters.out.persistence.entity.RecordEntity;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class RecordEntityMapper {

    private final ProductEntityMapper productEntityMapper;


    public RecordEntityMapper(ProductEntityMapper productEntityMapper) {
        this.productEntityMapper = productEntityMapper;
    }

    public Record toDomain(RecordEntity entity)
    {
        Record record = new Record();
        record.setId(entity.getId());
        if(entity.getProduct() != null) record.setProduct(productEntityMapper.toDomain(entity.getProduct()));
        record.setAmount(entity.getAmount());
        record.setProductSource(entity.getProductSource());
        record.setUnitPrice(entity.getUnitPrice());
        return record;
    }

    public RecordEntity toEntity(Record record)
    {
        RecordEntity entity = new RecordEntity();
        entity.setAmount(record.getAmount());
        entity.setProductSource(record.getProductSource());
        entity.setUnitPrice(record.getUnitPrice());
        return entity;
    }

    public List<Record> toListDomain(List<RecordEntity> entities)
    {
        return entities.stream().map(this::toDomain).toList();
    }

    public List<RecordEntity> toListEntity(List<Record> records)
    {
        return records.stream().map(this::toEntity).toList();
    }
}

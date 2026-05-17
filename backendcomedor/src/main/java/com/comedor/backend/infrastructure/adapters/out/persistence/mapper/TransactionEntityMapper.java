package com.comedor.backend.infrastructure.adapters.out.persistence.mapper;

import com.comedor.backend.domain.model.Transactions;
import com.comedor.backend.infrastructure.adapters.out.persistence.entity.TransactionsEntity;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class TransactionEntityMapper {
    private final ProductEntityMapper productEntityMapper;
    private final UserEntityMapper userEntityMapper;

    public TransactionEntityMapper(ProductEntityMapper productEntityMapper, UserEntityMapper userEntityMapper) {
        this.productEntityMapper = productEntityMapper;
        this.userEntityMapper = userEntityMapper;
    }

    public Transactions toDomain(TransactionsEntity entity)
    {
        Transactions domain = new Transactions();
        domain.setId(entity.getId());
        domain.setProduct(productEntityMapper.toDomain(entity.getProduct()));
        domain.setUser(userEntityMapper.toDomain(entity.getUser()));
        domain.setDateTime(entity.getDateTime());
        domain.setType(entity.getType());
        domain.setAmount(entity.getAmount());
        domain.setCurrentStock(entity.getCurrentStock());
        domain.setFinalStock(entity.getFinalStock());
        return domain;
    }

    public TransactionsEntity toEntity(Transactions domain)
    {
        TransactionsEntity entity = new TransactionsEntity();
        entity.setId(domain.getId());
        entity.setDateTime(domain.getDateTime());
        entity.setType(domain.getType());
        entity.setAmount(domain.getAmount());
        entity.setCurrentStock(domain.getCurrentStock());
        entity.setFinalStock(domain.getFinalStock());
        return entity;
    }

    public List<Transactions> toListDomain(List<TransactionsEntity> entities){
        return entities.stream().map(this::toDomain).toList();
    }
}

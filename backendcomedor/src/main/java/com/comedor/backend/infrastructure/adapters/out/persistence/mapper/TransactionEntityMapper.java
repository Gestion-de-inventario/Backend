package com.comedor.backend.infrastructure.adapters.out.persistence.mapper;

import com.comedor.backend.domain.model.Transactions;
import com.comedor.backend.infrastructure.adapters.out.persistence.entity.TransactionsEntity;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class TransactionEntityMapper {
    private final UserEntityMapper userEntityMapper;

    public TransactionEntityMapper(UserEntityMapper userEntityMapper) {
        this.userEntityMapper = userEntityMapper;
    }

    public Transactions toDomain(TransactionsEntity entity)
    {
        Transactions domain = new Transactions();
        domain.setId(entity.getId());
        domain.setUser(userEntityMapper.toDomain(entity.getUser()));
        domain.setDateTime(entity.getDateTime());
        domain.setType(entity.getType());
        domain.setSource(entity.getSource());
        domain.setAmount(entity.getAmount());
        domain.setCurrentStock(entity.getCurrentStock());
        domain.setFinalStock(entity.getFinalStock());

        if(entity.getReferenceId() != null)
        {
            domain.setReferenceId(entity.getReferenceId());
        }
        domain.setReferenceType(entity.getReferenceType());
        domain.setItemName(entity.getItemName());
        return domain;
    }

    public TransactionsEntity toEntity(Transactions domain)
    {
        TransactionsEntity entity = new TransactionsEntity();
        entity.setId(domain.getId());
        entity.setDateTime(domain.getDateTime());
        entity.setType(domain.getType());
        entity.setSource(domain.getSource());
        entity.setAmount(domain.getAmount());
        entity.setCurrentStock(domain.getCurrentStock());
        entity.setFinalStock(domain.getFinalStock());
        if(domain.getReferenceId() != null){
            entity.setReferenceId(domain.getReferenceId());
        }
        entity.setReferenceType(domain.getReferenceType());
        entity.setItemName(domain.getItemName());
        return entity;
    }

    public List<Transactions> toListDomain(List<TransactionsEntity> entities){
        return entities.stream().map(this::toDomain).toList();
    }
}

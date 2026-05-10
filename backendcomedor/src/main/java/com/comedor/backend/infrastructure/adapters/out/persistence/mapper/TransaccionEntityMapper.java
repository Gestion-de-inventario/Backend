package com.comedor.backend.infrastructure.adapters.out.persistence.mapper;

import com.comedor.backend.domain.model.Transacciones;
import com.comedor.backend.infrastructure.adapters.out.persistence.entity.TransaccionesEntity;
import lombok.Data;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class TransaccionEntityMapper {
    private final ProductoEntityMapper productoEntityMapper;
    private final UsuarioEntityMapper usuarioEntityMapper;

    public TransaccionEntityMapper(ProductoEntityMapper productoEntityMapper, UsuarioEntityMapper usuarioEntityMapper) {
        this.productoEntityMapper = productoEntityMapper;
        this.usuarioEntityMapper = usuarioEntityMapper;
    }

    public Transacciones toDomain(TransaccionesEntity entity)
    {
        Transacciones domain = new Transacciones();
        domain.setId(entity.getId());
        domain.setProduct(productoEntityMapper.toDomain(entity.getProduct()));
        domain.setUser(usuarioEntityMapper.toDomain(entity.getUser()));
        domain.setDateTime(entity.getDateTime());
        domain.setType(entity.getType());
        domain.setAmount(entity.getAmount());
        return domain;
    }

    public TransaccionesEntity toEntity(Transacciones domain)
    {
        TransaccionesEntity entity = new TransaccionesEntity();
        entity.setId(domain.getId());
        entity.setDateTime(domain.getDateTime());
        entity.setType(domain.getType());
        entity.setAmount(domain.getAmount());
        return entity;
    }

    public List<Transacciones> toListDomain(List<TransaccionesEntity> entities){
        return entities.stream().map(this::toDomain).toList();
    }
}

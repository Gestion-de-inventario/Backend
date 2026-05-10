package com.comedor.backend.infrastructure.adapters.out.persistence.mapper;

import com.comedor.backend.domain.model.Registro;
import com.comedor.backend.infrastructure.adapters.out.persistence.entity.RegistroEntity;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class RegistroEntityMapper {

    private final ProductoEntityMapper productoEntityMapper;


    public RegistroEntityMapper(ProductoEntityMapper productoEntityMapper) {
        this.productoEntityMapper = productoEntityMapper;
    }

    public Registro toDomain(RegistroEntity entity)
    {
        Registro registro = new Registro();
        registro.setId(entity.getId());
        if(entity.getProduct() != null)registro.setProduct(productoEntityMapper.toDomain(entity.getProduct()));
        registro.setAmount(entity.getAmount());
        registro.setFuenteProducto(entity.getFuenteProducto());
        registro.setUnitPrice(entity.getUnitPrice());
        return registro;
    }

    public RegistroEntity toEntity(Registro registro)
    {
        RegistroEntity entity = new RegistroEntity();
        entity.setAmount(registro.getAmount());
        entity.setFuenteProducto(registro.getFuenteProducto());
        entity.setUnitPrice(registro.getUnitPrice());
        return entity;
    }

    public List<Registro> toListDomain(List<RegistroEntity> entities)
    {
        return entities.stream().map(this::toDomain).toList();
    }

    public List<RegistroEntity> toListEntity(List<Registro> registros)
    {
        return registros.stream().map(this::toEntity).toList();
    }
}

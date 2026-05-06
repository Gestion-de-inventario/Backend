package com.comedor.backend.infrastructure.adapters.out.persistence.mapper;

import com.comedor.backend.domain.model.Producto;
import com.comedor.backend.infrastructure.adapters.out.persistence.entity.ProductoEntity;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ProductoEntityMapper {
    private final CategoriaEntityMapper categoriaEntityMapper;
    private final EtiquetaEntityMapper etiquetaEntityMapper;
    public ProductoEntityMapper(CategoriaEntityMapper categoriaEntityMapper, EtiquetaEntityMapper etiquetaEntityMapper) {
        this.categoriaEntityMapper = categoriaEntityMapper;
        this.etiquetaEntityMapper = etiquetaEntityMapper;
    }

    public Producto toDomain(ProductoEntity productoEntity)
    {
        if(productoEntity == null)
            return null;
        Producto producto = new Producto();
        producto.setId(productoEntity.getId());
        producto.setName(productoEntity.getName());
        producto.setCategoria(categoriaEntityMapper.toDomain(productoEntity.getCategoria()));
        producto.setEtiqueta(etiquetaEntityMapper.toDomain(productoEntity.getEtiqueta()));
        producto.setUnit(productoEntity.getUnit());
        producto.setStatus(productoEntity.getStatus());
        producto.setStock(productoEntity.getStock());
        producto.setReorderPoint(productoEntity.getReorderPoint());
        return producto;
    }
    public ProductoEntity toEntity(Producto producto) {
        if(producto == null)
            return null;
        ProductoEntity productoEntity = new ProductoEntity();
        productoEntity.setName(producto.getName());
        productoEntity.setCategoria(categoriaEntityMapper.toEntity(producto.getCategoria()));
        productoEntity.setEtiqueta(etiquetaEntityMapper.toEntity(producto.getEtiqueta()));
        productoEntity.setUnit(producto.getUnit());
        productoEntity.setStatus(producto.getStatus());
        productoEntity.setStock(producto.getStock());
        productoEntity.setReorderPoint(producto.getReorderPoint());
        return productoEntity;
    }

    public List<Producto> toListDomain(List<ProductoEntity> entities) {
        return entities.stream().map(this::toDomain).toList();
    }
}

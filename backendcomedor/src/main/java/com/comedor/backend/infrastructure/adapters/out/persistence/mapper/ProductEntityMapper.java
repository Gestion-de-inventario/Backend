package com.comedor.backend.infrastructure.adapters.out.persistence.mapper;

import com.comedor.backend.domain.model.Product;
import com.comedor.backend.infrastructure.adapters.out.persistence.entity.ProductEntity;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ProductEntityMapper {
    private final CategoryEntityMapper categoryEntityMapper;
    private final TagEntityMapper tagEntityMapper;
    public ProductEntityMapper(CategoryEntityMapper categoryEntityMapper, TagEntityMapper tagEntityMapper) {
        this.categoryEntityMapper = categoryEntityMapper;
        this.tagEntityMapper = tagEntityMapper;
    }

    public Product toDomain(ProductEntity productEntity)
    {
        if(productEntity == null)
            return null;
        Product product = new Product();
        product.setId(productEntity.getId());
        product.setName(productEntity.getName());
        product.setCategory(categoryEntityMapper.toDomain(productEntity.getCategory()));
        product.setTag(tagEntityMapper.toDomain(productEntity.getTag()));
        product.setUnit(productEntity.getUnit());
        product.setStatus(productEntity.getStatus());
        product.setStock(productEntity.getStock());
        product.setReorderPoint(productEntity.getReorderPoint());
        return product;
    }
    public ProductEntity toEntity(Product product) {
        if(product == null)
            return null;
        ProductEntity productEntity = new ProductEntity();
        productEntity.setName(product.getName());
        productEntity.setCategory(categoryEntityMapper.toEntity(product.getCategory()));
        productEntity.setTag(tagEntityMapper.toEntity(product.getTag()));
        productEntity.setUnit(product.getUnit());
        productEntity.setStatus(product.getStatus());
        productEntity.setStock(product.getStock());
        productEntity.setReorderPoint(product.getReorderPoint());
        return productEntity;
    }

    public List<Product> toListDomain(List<ProductEntity> entities) {
        return entities.stream().map(this::toDomain).toList();
    }
}

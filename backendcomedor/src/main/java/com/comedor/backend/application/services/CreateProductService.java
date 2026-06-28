package com.comedor.backend.application.services;

import com.comedor.backend.application.common.mapper.ProductMapper;
import com.comedor.backend.application.ports.in.CreateProductUseCase;
import com.comedor.backend.application.ports.out.CategoryRepositoryPort;
import com.comedor.backend.application.ports.out.TagRepositoryPort;
import com.comedor.backend.application.ports.out.ProductRepositoryPort;
import com.comedor.backend.domain.exceptions.ExistingProductException;
import com.comedor.backend.domain.model.Category;
import com.comedor.backend.domain.model.Tag;
import com.comedor.backend.domain.model.Product;
import com.comedor.backend.infrastructure.adapters.in.web.dto.request.ProductRequestDTO;
import com.comedor.backend.infrastructure.adapters.in.web.dto.response.ProductResponseDTO;

import java.math.BigDecimal;

public class CreateProductService implements CreateProductUseCase {

    private final ProductRepositoryPort productRepositoryPort;
    private final ProductMapper productMapper;
    private final CategoryRepositoryPort categoryRepositoryPort;
    private final TagRepositoryPort tagRepositoryPort;

    public CreateProductService(ProductRepositoryPort productRepositoryPort, ProductMapper productMapper, CategoryRepositoryPort categoryRepositoryPort, TagRepositoryPort tagRepositoryPort) {
        this.productRepositoryPort = productRepositoryPort;
        this.productMapper = productMapper;
        this.categoryRepositoryPort = categoryRepositoryPort;
        this.tagRepositoryPort = tagRepositoryPort;
    }

    @Override
    public ProductResponseDTO crearProducto(ProductRequestDTO productRequestDTO) {
        if(productRepositoryPort.existByName(productRequestDTO.getName().toUpperCase()))
        {
            throw new ExistingProductException("Ya existe un producto con ese nombre :"+ productRequestDTO.getName());
        }
        if (productRequestDTO.getCategoryId() == null) {
            throw new IllegalArgumentException("La categoría es obligatoria");
        }

        Category category = categoryRepositoryPort.getCategoryById(productRequestDTO.getCategoryId());

        Tag tag = null;
        if (productRequestDTO.getTagId() != null) {
            tag = tagRepositoryPort.getTagById(productRequestDTO.getTagId());
        }

        Product product = productMapper.toDomain(productRequestDTO);
        product.setStock(BigDecimal.ZERO);
        product.setCategory(category);
        product.setTag(tag);
        return  productMapper.productoResponseDTO(productRepositoryPort.createProducto(product));
    }
}

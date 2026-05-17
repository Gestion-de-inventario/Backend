package com.comedor.backend.application.services;

import com.comedor.backend.application.common.mapper.ProductMapper;
import com.comedor.backend.application.ports.in.CrearProductoUseCase;
import com.comedor.backend.application.ports.out.CategoryRepositoryPort;
import com.comedor.backend.application.ports.out.TagRepositoryPort;
import com.comedor.backend.application.ports.out.ProductRepositoryPort;
import com.comedor.backend.domain.exceptions.ProductoExistenteException;
import com.comedor.backend.domain.model.Category;
import com.comedor.backend.domain.model.Tag;
import com.comedor.backend.domain.model.Product;
import com.comedor.backend.infrastructure.adapters.in.web.dto.request.ProductoRequestDTO;
import com.comedor.backend.infrastructure.adapters.in.web.dto.response.ProductoResponseDTO;

public class CrearProductoService implements CrearProductoUseCase {

    private final ProductRepositoryPort productRepositoryPort;
    private final ProductMapper productMapper;
    private final CategoryRepositoryPort categoryRepositoryPort;
    private final TagRepositoryPort tagRepositoryPort;

    public CrearProductoService(ProductRepositoryPort productRepositoryPort, ProductMapper productMapper, CategoryRepositoryPort categoryRepositoryPort, TagRepositoryPort tagRepositoryPort) {
        this.productRepositoryPort = productRepositoryPort;
        this.productMapper = productMapper;
        this.categoryRepositoryPort = categoryRepositoryPort;
        this.tagRepositoryPort = tagRepositoryPort;
    }

    @Override
    public ProductoResponseDTO crearProducto(ProductoRequestDTO productoRequestDTO) {
        if(productRepositoryPort.existByName(productoRequestDTO.getName().toUpperCase()))
        {
            throw new ProductoExistenteException("Ya existe un producto con ese nombre :"+productoRequestDTO.getName());
        }
        if (productoRequestDTO.getCategoryId() == null) {
            throw new IllegalArgumentException("La categoría es obligatoria");
        }

        Category category = categoryRepositoryPort.getCategoryById(productoRequestDTO.getCategoryId());

        Tag tag = null;
        if (productoRequestDTO.getTagId() != null) {
            tag = tagRepositoryPort.getTagById(productoRequestDTO.getTagId());
        }

        Product product = productMapper.toDomain(productoRequestDTO);
        product.setCategory(category);
        product.setTag(tag);
        return  productMapper.productoResponseDTO(productRepositoryPort.createProducto(product));
    }
}

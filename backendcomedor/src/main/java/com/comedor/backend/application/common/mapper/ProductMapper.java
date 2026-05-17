package com.comedor.backend.application.common.mapper;


import com.comedor.backend.domain.model.Category;
import com.comedor.backend.domain.model.Tag;
import com.comedor.backend.domain.model.Product;
import com.comedor.backend.infrastructure.adapters.in.web.dto.request.ProductoRequestDTO;
import com.comedor.backend.infrastructure.adapters.in.web.dto.response.ProductoResponseDTO;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ProductMapper {
    public Product toDomain(ProductoRequestDTO dto) {

        Product product = new Product();

        product.setName(dto.getName());

        if (dto.getCategoryId() != null) {
            Category category = new Category();
            category.setId(dto.getCategoryId());
            product.setCategory(category);
        }

        if (dto.getTagId() != null) {
            Tag tag = new Tag();
            tag.setId(dto.getTagId());
            product.setTag(tag);
        }

        product.setUnit(dto.getUnit());
        product.setStock(dto.getStock());
        product.setReorderPoint(dto.getReorderPoint());

        return product;
    }

    public ProductoResponseDTO productoResponseDTO(Product product)
    {
        if (product == null)
            return null;
        ProductoResponseDTO productoResponseDTO = new ProductoResponseDTO();
        productoResponseDTO.setId(product.getId());
        productoResponseDTO.setName(product.getName());
        productoResponseDTO.setStatus(product.getStatus());
        productoResponseDTO.setCategoryId(product.getCategory().getId());
        productoResponseDTO.setCategoryName(product.getCategory().getName());
        productoResponseDTO.setCategoryState(product.getCategory().getStatus());
        if (product.getTag()!=null){
            productoResponseDTO.setTagId(product.getTag().getId());
            productoResponseDTO.setTagName(product.getTag().getName());
            productoResponseDTO.setTagState(product.getTag().getStatus());
        }
        productoResponseDTO.setUnit(product.getUnit());
        productoResponseDTO.setStock(product.getStock());
        productoResponseDTO.setReorderPoint(product.getReorderPoint());
        return productoResponseDTO;
    }

    public List<ProductoResponseDTO> toListProductoResponseDTO (List<Product> products)
    {
        if (products == null)
            return null;
        return products.stream().map(this::productoResponseDTO).toList();
    }

}

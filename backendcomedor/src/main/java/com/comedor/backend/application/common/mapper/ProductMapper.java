package com.comedor.backend.application.common.mapper;


import com.comedor.backend.domain.model.Category;
import com.comedor.backend.domain.model.Tag;
import com.comedor.backend.domain.model.Product;
import com.comedor.backend.infrastructure.adapters.in.web.dto.request.ProductRequestDTO;
import com.comedor.backend.infrastructure.adapters.in.web.dto.response.ProductResponseDTO;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ProductMapper {
    public Product toDomain(ProductRequestDTO dto) {

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
        product.setReorderPoint(dto.getReorderPoint());

        return product;
    }

    public ProductResponseDTO productoResponseDTO(Product product)
    {
        if (product == null)
            return null;
        ProductResponseDTO productResponseDTO = new ProductResponseDTO();
        productResponseDTO.setId(product.getId());
        productResponseDTO.setName(product.getName());
        productResponseDTO.setStatus(product.getStatus());
        productResponseDTO.setCategoryId(product.getCategory().getId());
        productResponseDTO.setCategoryName(product.getCategory().getName());
        productResponseDTO.setCategoryState(product.getCategory().getStatus());
        if (product.getTag()!=null){
            productResponseDTO.setTagId(product.getTag().getId());
            productResponseDTO.setTagName(product.getTag().getName());
            productResponseDTO.setTagState(product.getTag().getStatus());
        }
        productResponseDTO.setUnit(product.getUnit());
        productResponseDTO.setStock(product.getStock());
        productResponseDTO.setReorderPoint(product.getReorderPoint());
        return productResponseDTO;
    }

    public List<ProductResponseDTO> toListProductoResponseDTO (List<Product> products)
    {
        if (products == null)
            return null;
        return products.stream().map(this::productoResponseDTO).toList();
    }

}

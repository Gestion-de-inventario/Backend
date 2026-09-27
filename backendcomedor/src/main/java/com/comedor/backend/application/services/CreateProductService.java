package com.comedor.backend.application.services;

import com.comedor.backend.application.common.mapper.ProductMapper;
import com.comedor.backend.application.ports.in.CreateProductUseCase;
import com.comedor.backend.application.ports.in.RegisterAuditUseCase;
import com.comedor.backend.application.ports.out.CategoryRepositoryPort;
import com.comedor.backend.application.ports.out.TagRepositoryPort;
import com.comedor.backend.application.ports.out.ProductRepositoryPort;
import com.comedor.backend.domain.exceptions.ExistingProductException;
import com.comedor.backend.domain.exceptions.InvalidProductUnitException;
import com.comedor.backend.domain.model.Category;
import com.comedor.backend.domain.model.Tag;
import com.comedor.backend.domain.model.Product;
import com.comedor.backend.domain.model.enums.AuditAction;
import com.comedor.backend.infrastructure.adapters.in.web.dto.request.AuditRequestDTO;
import com.comedor.backend.infrastructure.adapters.in.web.dto.request.ProductRequestDTO;
import com.comedor.backend.infrastructure.adapters.in.web.dto.response.ProductResponseDTO;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public class CreateProductService implements CreateProductUseCase {

    private final ProductRepositoryPort productRepositoryPort;
    private final ProductMapper productMapper;
    private final CategoryRepositoryPort categoryRepositoryPort;
    private final TagRepositoryPort tagRepositoryPort;
    private final RegisterAuditUseCase registerAuditUseCase;

    public CreateProductService(ProductRepositoryPort productRepositoryPort, ProductMapper productMapper, CategoryRepositoryPort categoryRepositoryPort, TagRepositoryPort tagRepositoryPort, RegisterAuditUseCase registerAuditUseCase) {
        this.productRepositoryPort = productRepositoryPort;
        this.productMapper = productMapper;
        this.categoryRepositoryPort = categoryRepositoryPort;
        this.tagRepositoryPort = tagRepositoryPort;
        this.registerAuditUseCase = registerAuditUseCase;
    }

    @Override
    public ProductResponseDTO crearProducto(ProductRequestDTO productRequestDTO) {

        String unidadNormalizada =
                normalizarUnidad(productRequestDTO.getUnit());

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
        product.setUnit(unidadNormalizada);
        product.setCategory(category);
        product.setTag(tag);
        Product savedProduct =
                productRepositoryPort.createProducto(product);
        registerAuditUseCase.registrar(
                new AuditRequestDTO(
                        "Producto",
                        savedProduct.getId(),
                        savedProduct.getName(),
                        AuditAction.CREACION,
                        Map.of(
                                "Nombre",
                                savedProduct.getName(),

                                "Categoría",
                                savedProduct.getCategory().getName(),

                                "Etiqueta",
                                savedProduct.getTag() != null
                                        ? savedProduct.getTag().getName()
                                        : "Sin etiqueta",

                                "Unidad",
                                savedProduct.getUnit(),

                                "Estado",
                                savedProduct.getStatus().toString(),

                                "Punto de reorden",
                                savedProduct.getReorderPoint() != null
                                        ? savedProduct.getReorderPoint().toString()
                                        : "0"
                        )
                )
        );
        return  productMapper.productoResponseDTO(savedProduct);
    }

    private String normalizarUnidad(String unit) {
        if (unit == null || unit.isBlank()) {
            throw new IllegalArgumentException("La unidad de medida es obligatoria");
        }

        String unidad = unit.trim().toUpperCase();

        return switch (unidad) {
            case "KILOGRAMO", "KILOGRAMOS", "KILO", "KILOS", "KG" -> "KG";
            case "LITRO", "LITROS", "L" -> "L";
            case "UNIDAD", "UNIDADES" -> "UNIDADES";
            case "SACO", "SACOS" -> "SACOS";
            case "LATA", "LATAS" -> "LATAS";
            case "BOLSA", "BOLSAS" -> "BOLSAS";
            default -> throw new InvalidProductUnitException("Unidad de medida no permitida");
        };
    }
}

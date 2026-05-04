package com.comedor.backend.infrastructure.adapters.in.web.dto.request;

import com.comedor.backend.domain.model.Categoria;
import com.comedor.backend.domain.model.Subcategoria;
import com.comedor.backend.domain.model.enums.Estado;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class ProductoRequestDTO {
    private String name;
    private int categoryId;
    private int subcategoryId;
    private String unit;
    private BigDecimal stock;
    private BigDecimal reorderPoint;
}

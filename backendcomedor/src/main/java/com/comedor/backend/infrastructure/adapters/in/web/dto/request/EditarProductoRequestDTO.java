package com.comedor.backend.infrastructure.adapters.in.web.dto.request;

import com.comedor.backend.domain.model.Category;
import com.comedor.backend.domain.model.Tag;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class EditarProductoRequestDTO {
    private String name;
    private Category category;
    private Tag tag;
    private String unit;
    private BigDecimal reorderPoint;
}

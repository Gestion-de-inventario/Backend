package com.comedor.backend.infrastructure.adapters.in.web.dto.response;

import com.comedor.backend.domain.model.enums.Status;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class ProductResponseDTO {
    private int id;
    private String name;
    private Status status;
    private int categoryId;
    private String categoryName;
    private Status categoryState;
    private int tagId;
    private String TagName;
    private Status TagState;
    private String unit;
    private BigDecimal stock;
    private BigDecimal reorderPoint;
}

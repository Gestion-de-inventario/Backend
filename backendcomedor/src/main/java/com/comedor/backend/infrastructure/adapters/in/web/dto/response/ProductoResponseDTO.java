package com.comedor.backend.infrastructure.adapters.in.web.dto.response;

import com.comedor.backend.domain.model.enums.Estado;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class ProductoResponseDTO {
    private int id;
    private String name;
    private Estado status;
    private int categoryId;
    private String categoryName;
    private Estado categoryState;
    private int etiquetaId;
    private String EtiquetaName;
    private Estado EtiquetaState;
    private String unit;
    private BigDecimal stock;
    private BigDecimal reorderPoint;
}

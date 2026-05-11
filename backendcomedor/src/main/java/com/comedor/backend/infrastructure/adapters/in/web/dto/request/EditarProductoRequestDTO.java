package com.comedor.backend.infrastructure.adapters.in.web.dto.request;

import com.comedor.backend.domain.model.Categoria;
import com.comedor.backend.domain.model.Etiqueta;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class EditarProductoRequestDTO {
    private String name;
    private Categoria categoria;
    private Etiqueta etiqueta;
    private String unit;
    private BigDecimal reorderPoint;
}

package com.comedor.backend.infrastructure.adapters.in.web.dto.response;

import com.comedor.backend.domain.model.enums.FuenteProducto;
import lombok.Data;

import java.math.BigDecimal;
@Data
public class RegistroProductoResponseDTO {
    private int productoId;
    private String productName;
    private String productCategory;
    private String productUnit;
    private FuenteProducto sourceProduct;
    private BigDecimal amount;
    private BigDecimal spentAmount;
}

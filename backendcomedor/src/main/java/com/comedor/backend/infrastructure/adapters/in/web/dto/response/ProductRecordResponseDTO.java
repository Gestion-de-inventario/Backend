package com.comedor.backend.infrastructure.adapters.in.web.dto.response;

import com.comedor.backend.domain.model.enums.ProductSource;
import lombok.Data;

import java.math.BigDecimal;
@Data
public class ProductRecordResponseDTO {
    private int productoId;
    private String productName;
    private String productCategory;
    private String productUnit;
    private ProductSource sourceProduct;
    private BigDecimal amount;
    private BigDecimal spentAmount;
}

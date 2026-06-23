package com.comedor.backend.infrastructure.adapters.in.web.dto.request;

import com.comedor.backend.domain.model.enums.ProductSource;
import lombok.Data;

import java.math.BigDecimal;
@Data
public class ProductRecordRequestDTO {
    private int productoId;
    private BigDecimal amount;
    private ProductSource productSource;
    private BigDecimal unitPrice;
}

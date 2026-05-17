package com.comedor.backend.infrastructure.adapters.in.web.dto.request;

import com.comedor.backend.domain.model.enums.FuenteProducto;
import lombok.Data;

import java.math.BigDecimal;
@Data
public class RegistroProductoRequestDTO {
    private int productoId;
    private BigDecimal amount;
    private FuenteProducto productSource;
    private BigDecimal unitPrice;
}

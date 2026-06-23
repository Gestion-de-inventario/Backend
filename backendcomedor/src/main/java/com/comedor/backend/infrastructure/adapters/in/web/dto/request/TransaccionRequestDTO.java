package com.comedor.backend.infrastructure.adapters.in.web.dto.request;

import com.comedor.backend.domain.model.enums.FuenteTransaccion;
import com.comedor.backend.domain.model.enums.TipoMovimiento;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
@Data
public class TransaccionRequestDTO {
    private int productId;
    private Integer userId;
    private TipoMovimiento type;
    private FuenteTransaccion source;
    private BigDecimal amount;
    private LocalDateTime dateTime;

}

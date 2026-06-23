package com.comedor.backend.infrastructure.adapters.in.web.dto.request;

import com.comedor.backend.domain.model.enums.TransactionSource;
import com.comedor.backend.domain.model.enums.MovementType;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
@Data
public class TransactionRequestDTO {
    private int productId;
    private Integer userId;
    private MovementType type;
    private TransactionSource source;
    private BigDecimal amount;
    private LocalDateTime dateTime;

}

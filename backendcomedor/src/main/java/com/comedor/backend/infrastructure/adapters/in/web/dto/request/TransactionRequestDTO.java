package com.comedor.backend.infrastructure.adapters.in.web.dto.request;

import com.comedor.backend.domain.model.enums.TransactionReferenceType;
import com.comedor.backend.domain.model.enums.TransactionSource;
import com.comedor.backend.domain.model.enums.MovementType;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
@Data
public class TransactionRequestDTO {
    private TransactionReferenceType referenceType;
    private Integer referenceId;
    private String itemName;
    private MovementType type;
    private BigDecimal amount;
    private BigDecimal currentStock;
    private TransactionSource source;
    private Integer userId;
    private LocalDateTime dateTime;
}

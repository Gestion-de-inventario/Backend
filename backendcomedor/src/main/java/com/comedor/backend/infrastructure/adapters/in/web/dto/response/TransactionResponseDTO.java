package com.comedor.backend.infrastructure.adapters.in.web.dto.response;

import com.comedor.backend.domain.model.enums.TransactionSource;
import com.comedor.backend.domain.model.enums.MovementType;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
@Data
public class TransactionResponseDTO {
    private int id;
    private LocalDateTime dateTime;
    private MovementType type;
    private TransactionSource source;
    private BigDecimal amount;
    private BigDecimal currentStock;
    private BigDecimal finalStock;

    private Integer productId;
    private String productName;


    private Integer userId;
    private String userName;
    private String personaName;
    private String personaLastName;
}

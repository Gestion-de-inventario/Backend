package com.comedor.backend.infrastructure.adapters.in.web.dto.response;

import com.comedor.backend.domain.model.Producto;
import com.comedor.backend.domain.model.Usuario;
import com.comedor.backend.domain.model.enums.TipoMovimiento;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
@Data
public class TransaccionResponseDTO {
    private int id;
    private LocalDateTime dateTime;
    private TipoMovimiento type;
    private BigDecimal amount;

    private Integer productId;
    private String productName;

    private Integer userId;
    private String userName;
    private String personaName;
    private String personaLastName;
}

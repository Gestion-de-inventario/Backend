package com.comedor.backend.domain.model;

import com.comedor.backend.domain.model.enums.TipoMovimiento;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class Transacciones {
    private int id;
    private int product_id;
    private int user_id;
    private LocalDateTime date_time;
    private TipoMovimiento type;
    private BigDecimal amount;

}

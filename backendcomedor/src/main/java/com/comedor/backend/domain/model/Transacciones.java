package com.comedor.backend.domain.model;

import com.comedor.backend.domain.model.enums.TipoMovimiento;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class Transacciones {
    private int id;
    private Producto product;
    private Usuario user;
    private LocalDateTime dateTime;
    private TipoMovimiento type;
    private BigDecimal amount;

}

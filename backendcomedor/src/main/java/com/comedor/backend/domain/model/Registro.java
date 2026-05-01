package com.comedor.backend.domain.model;

import com.comedor.backend.domain.model.enums.FuenteProducto;

import java.math.BigDecimal;

public class Registro {
    private int id;
    private Producto product;
    private BigDecimal amount;
    private FuenteProducto fuenteProducto;
    private BigDecimal unit_price;
    private BigDecimal total_spent;

}
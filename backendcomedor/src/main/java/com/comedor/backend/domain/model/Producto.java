package com.comedor.backend.domain.model;

import com.comedor.backend.domain.model.enums.Estado;

import java.math.BigDecimal;

public class Producto {
    private Long id;
    private String name;
    private Categoria categoria;
    private Subcategoria subcategoria;
    private String unit;
    private Estado estado = Estado.ACTIVO;
    private BigDecimal stock;
    private BigDecimal reorder_point;
}

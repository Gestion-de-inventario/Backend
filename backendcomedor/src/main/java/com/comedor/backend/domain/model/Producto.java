package com.comedor.backend.domain.model;

import com.comedor.backend.domain.model.enums.Estado;

import java.math.BigDecimal;

public class Producto {
    private int id;
    private String name;
    private Categoria categoria;
    private Subcategoria subcategoria;
    private String unit;
    private Estado status = Estado.ACTIVO;
    private BigDecimal stock;
    private BigDecimal reorderPoint;

    public void setName(String name) {
        this.name = name.toUpperCase();
    }
}

package com.comedor.backend.domain.model;

import com.comedor.backend.domain.model.enums.FuenteProducto;

import java.math.BigDecimal;

public class Registro {
    private int id;
    private Producto product;
    private BigDecimal amount;
    private FuenteProducto fuenteProducto;
    private BigDecimal unitPrice = BigDecimal.ZERO;

    public int getId() {
        return id;
    }

    public Producto getProduct() {
        return product;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public FuenteProducto getFuenteProducto() {
        return fuenteProducto;
    }

    public BigDecimal getUnitPrice() {
        return unitPrice;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setProduct(Producto product) {
        this.product = product;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public void setFuenteProducto(FuenteProducto fuenteProducto) {
        this.fuenteProducto = fuenteProducto;
    }

    public void setUnitPrice(BigDecimal unitPrice) {
        this.unitPrice = unitPrice;
    }
}
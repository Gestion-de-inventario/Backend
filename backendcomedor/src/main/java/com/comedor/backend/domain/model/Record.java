package com.comedor.backend.domain.model;

import com.comedor.backend.domain.model.enums.FuenteProducto;

import java.math.BigDecimal;

public class Record {
    private int id;
    private Product product;
    private BigDecimal amount;
    private FuenteProducto productSource;
    private BigDecimal unitPrice = BigDecimal.ZERO;

    public int getId() {
        return id;
    }

    public Product getProduct() {
        return product;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public FuenteProducto getProductSource() {
        return productSource;
    }

    public BigDecimal getUnitPrice() {
        return unitPrice;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public void setProductSource(FuenteProducto fuenteProducto) {
        this.productSource = productSource;
    }

    public void setUnitPrice(BigDecimal unitPrice) {
        this.unitPrice = unitPrice;
    }
}
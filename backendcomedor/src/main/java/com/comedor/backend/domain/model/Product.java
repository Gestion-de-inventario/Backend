package com.comedor.backend.domain.model;

import com.comedor.backend.domain.model.enums.Estado;

import java.math.BigDecimal;

public class Product {
    private int id;
    private String name;
    private Category category;
    private Tag tag;
    private String unit;
    private Estado status = Estado.ACTIVO;
    private BigDecimal stock;
    private BigDecimal reorderPoint;

    public void setName(String name) {
        this.name = name.toUpperCase();
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public void setTag(Tag tag) {
        this.tag = tag;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setUnit(String unit) {
        this.unit = unit.toUpperCase();
    }

    public void setStatus(Estado status) {
        this.status = status;
    }

    public void setStock(BigDecimal stock) {
        this.stock = stock;
    }

    public void setReorderPoint(BigDecimal reorderPoint) {
        this.reorderPoint = reorderPoint;
    }
    public int getId() {
        return id;
    }
    public String getName() {
        return name;
    }

    public Category getCategory() {
        return category;
    }

    public Tag getTag() {
        return tag;
    }

    public String getUnit() {
        return unit;
    }

    public Estado getStatus() {
        return status;
    }

    public BigDecimal getStock() {
        return stock;
    }

    public BigDecimal getReorderPoint() {
        return reorderPoint;
    }
}

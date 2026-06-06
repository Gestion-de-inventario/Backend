package com.comedor.backend.domain.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class InventoryLot {
    private Integer id;

    private Product product;

    private BigDecimal quantity = BigDecimal.ZERO;

    private BigDecimal remainingQuantity = BigDecimal.ZERO;

    private BigDecimal unitCost = BigDecimal.ZERO;

    private LocalDateTime entryDate;

    public void setId(Integer id) {
        this.id = id;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public void setQuantity(BigDecimal quantity) {
        this.quantity = quantity;
    }

    public void setRemainingQuantity(BigDecimal remainingQuantity) {
        this.remainingQuantity = remainingQuantity;
    }

    public void setUnitCost(BigDecimal unitCost) {
        this.unitCost = unitCost;
    }

    public void setEntryDate(LocalDateTime entryDate) {
        this.entryDate = entryDate;
    }

    public Integer getId() {
        return id;
    }

    public Product getProduct() {
        return product;
    }

    public BigDecimal getQuantity() {
        return quantity;
    }

    public BigDecimal getRemainingQuantity() {
        return remainingQuantity;
    }

    public BigDecimal getUnitCost() {
        return unitCost;
    }

    public LocalDateTime getEntryDate() {
        return entryDate;
    }
}

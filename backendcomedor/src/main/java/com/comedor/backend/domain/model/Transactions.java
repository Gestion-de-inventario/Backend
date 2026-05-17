package com.comedor.backend.domain.model;

import com.comedor.backend.domain.model.enums.TipoMovimiento;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class Transactions {
    private int id;
    private Product product;
    private User user;
    private LocalDateTime dateTime;
    private TipoMovimiento type;
    private BigDecimal amount;
    private BigDecimal currentStock;
    private BigDecimal finalStock;

    public void setId(int id) {
        this.id = id;
    }

    public void setCurrentStock(BigDecimal currentStock) {
        this.currentStock = currentStock;
    }

    public void setFinalStock(BigDecimal finalStock) {
        this.finalStock = finalStock;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public void setDateTime(LocalDateTime dateTime) {
        this.dateTime = dateTime;
    }

    public void setType(TipoMovimiento type) {
        this.type = type;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public int getId() {
        return id;
    }

    public Product getProduct() {
        return product;
    }

    public User getUser() {
        return user;
    }

    public LocalDateTime getDateTime() {
        return dateTime;
    }

    public TipoMovimiento getType() {
        return type;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public BigDecimal getCurrentStock() {
        return currentStock;
    }

    public BigDecimal getFinalStock() {
        return finalStock;
    }
}

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

    public void setId(int id) {
        this.id = id;
    }

    public void setProduct(Producto product) {
        this.product = product;
    }

    public void setUser(Usuario user) {
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

    public Producto getProduct() {
        return product;
    }

    public Usuario getUser() {
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
}

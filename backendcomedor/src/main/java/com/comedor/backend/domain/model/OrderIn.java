package com.comedor.backend.domain.model;

import com.comedor.backend.domain.model.enums.EstadoOrden;
import com.comedor.backend.domain.model.enums.FuenteProducto;

import java.math.BigDecimal;
import java.time.LocalDate;

public class OrderIn {

    private String reference;

    private Integer id;

    private FuenteProducto source;

    private LocalDate date;

    private EstadoOrden status;

    private BigDecimal totalSpent;

    public OrderIn() {
    }

    public String getReference() {
        return reference;
    }

    public void setReference(String reference) {
        this.reference = reference;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public FuenteProducto getSource() {
        return source;
    }

    public void setSource(FuenteProducto source) {
        this.source = source;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public EstadoOrden getStatus() {
        return status;
    }

    public void setStatus(EstadoOrden status) {
        this.status = status;
    }

    public BigDecimal getTotalSpent() {
        return totalSpent;
    }

    public void setTotalSpent(BigDecimal totalSpent) {
        this.totalSpent = totalSpent;
    }


}
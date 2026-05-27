package com.comedor.backend.domain.model;

import com.comedor.backend.domain.model.enums.EstadoOrden;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Purchase {

    private Integer id;

    private BigDecimal totalSpent = BigDecimal.ZERO;

    private EstadoOrden status;

    private LocalDate purchaseDate;

    private List<PurchaseDetail> details = new ArrayList<>();

    public Purchase() {
    }

    public Purchase(Integer id,
                    BigDecimal totalSpent,
                    EstadoOrden status,
                    LocalDate purchaseDate,
                    List<PurchaseDetail> details) {

        this.id = id;
        this.totalSpent = totalSpent;
        this.status = status;
        this.purchaseDate = purchaseDate;
        this.details = details;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public BigDecimal getTotalSpent() {
        return totalSpent;
    }

    public void setTotalSpent(BigDecimal totalSpent) {
        this.totalSpent = totalSpent;
    }

    public EstadoOrden getStatus() {
        return status;
    }

    public void setStatus(EstadoOrden status) {
        this.status = status;
    }

    public LocalDate getPurchaseDate() {
        return purchaseDate;
    }

    public void setPurchaseDate(LocalDate purchaseDate) {
        this.purchaseDate = purchaseDate;
    }

    public List<PurchaseDetail> getDetails() {
        return details;
    }

    public void setDetails(List<PurchaseDetail> details) {
        this.details = details;
    }
}
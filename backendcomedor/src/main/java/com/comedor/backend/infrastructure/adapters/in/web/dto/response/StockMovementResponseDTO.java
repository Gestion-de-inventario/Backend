package com.comedor.backend.infrastructure.adapters.in.web.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class StockMovementResponseDTO {

    private Integer id;
    private String productName;
    private BigDecimal quantityUsed;
    private BigDecimal unitCost;
    private BigDecimal totalCost;
    private LocalDateTime movementDate;

    public StockMovementResponseDTO(Integer id, String productName, BigDecimal quantityUsed, BigDecimal unitCost, BigDecimal totalCost, LocalDateTime movementDate) {
        this.id = id;
        this.productName = productName;
        this.quantityUsed = quantityUsed;
        this.unitCost = unitCost;
        this.totalCost = totalCost;
        this.movementDate = movementDate;
    }

    public StockMovementResponseDTO() {

    }

    public Integer getId() {
        return id;
    }

    public String getProductName() {
        return productName;
    }

    public BigDecimal getQuantityUsed() {
        return quantityUsed;
    }

    public BigDecimal getUnitCost() {
        return unitCost;
    }

    public BigDecimal getTotalCost() {
        return totalCost;
    }

    public LocalDateTime getMovementDate() {
        return movementDate;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public void setQuantityUsed(BigDecimal quantityUsed) {
        this.quantityUsed = quantityUsed;
    }

    public void setUnitCost(BigDecimal unitCost) {
        this.unitCost = unitCost;
    }

    public void setTotalCost(BigDecimal totalCost) {
        this.totalCost = totalCost;
    }

    public void setMovementDate(LocalDateTime movementDate) {
        this.movementDate = movementDate;
    }
}
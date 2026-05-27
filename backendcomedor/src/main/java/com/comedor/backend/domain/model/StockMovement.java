package com.comedor.backend.domain.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class StockMovement {

    private Integer id;

    private Product product;

    private PurchaseDetail purchaseDetail;

    private MenuReport menuReport;

    private BigDecimal quantityUsed = BigDecimal.ZERO;

    private BigDecimal unitCost = BigDecimal.ZERO;

    private BigDecimal totalCost = BigDecimal.ZERO;

    private LocalDateTime movementDate;

    public StockMovement() {
    }

    public StockMovement(Integer id,
                         Product product,
                         PurchaseDetail purchaseDetail,
                         MenuReport menuReport,
                         BigDecimal quantityUsed,
                         BigDecimal unitCost,
                         BigDecimal totalCost,
                         LocalDateTime movementDate) {

        this.id = id;
        this.product = product;
        this.purchaseDetail = purchaseDetail;
        this.menuReport = menuReport;
        this.quantityUsed = quantityUsed;
        this.unitCost = unitCost;
        this.totalCost = totalCost;
        this.movementDate = movementDate;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public PurchaseDetail getPurchaseDetail() {
        return purchaseDetail;
    }

    public void setPurchaseDetail(PurchaseDetail purchaseDetail) {
        this.purchaseDetail = purchaseDetail;
    }

    public MenuReport getMenuReport() {
        return menuReport;
    }

    public void setMenuReport(MenuReport menuReport) {
        this.menuReport = menuReport;
    }

    public BigDecimal getQuantityUsed() {
        return quantityUsed;
    }

    public void setQuantityUsed(BigDecimal quantityUsed) {
        this.quantityUsed = quantityUsed;
    }

    public BigDecimal getUnitCost() {
        return unitCost;
    }

    public void setUnitCost(BigDecimal unitCost) {
        this.unitCost = unitCost;
    }

    public BigDecimal getTotalCost() {
        return totalCost;
    }

    public void setTotalCost(BigDecimal totalCost) {
        this.totalCost = totalCost;
    }

    public LocalDateTime getMovementDate() {
        return movementDate;
    }

    public void setMovementDate(LocalDateTime movementDate) {
        this.movementDate = movementDate;
    }
}
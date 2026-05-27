package com.comedor.backend.domain.model;

import java.math.BigDecimal;

public class PurchaseDetail {

    private Integer id;

    private Purchase purchase;

    private Product product;

    private BigDecimal quantity = BigDecimal.ZERO;

    private BigDecimal remainingQuantity = BigDecimal.ZERO;

    private BigDecimal unitPrice = BigDecimal.ZERO;

    private BigDecimal subTotal = BigDecimal.ZERO;

    public PurchaseDetail() {
    }

    public PurchaseDetail(Integer id,
                          Purchase purchase,
                          Product product,
                          BigDecimal quantity,
                          BigDecimal remainingQuantity,
                          BigDecimal unitPrice,
                          BigDecimal subTotal) {

        this.id = id;
        this.purchase = purchase;
        this.product = product;
        this.quantity = quantity;
        this.remainingQuantity = remainingQuantity;
        this.unitPrice = unitPrice;
        this.subTotal = subTotal;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Purchase getPurchase() {
        return purchase;
    }

    public void setPurchase(Purchase purchase) {
        this.purchase = purchase;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public BigDecimal getQuantity() {
        return quantity;
    }

    public void setQuantity(BigDecimal quantity) {
        this.quantity = quantity;
    }

    public BigDecimal getRemainingQuantity() {
        return remainingQuantity;
    }

    public void setRemainingQuantity(BigDecimal remainingQuantity) {
        this.remainingQuantity = remainingQuantity;
    }

    public BigDecimal getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(BigDecimal unitPrice) {
        this.unitPrice = unitPrice;
    }

    public BigDecimal getSubTotal() {
        return subTotal;
    }

    public void setSubTotal(BigDecimal subTotal) {
        this.subTotal = subTotal;
    }
}
package com.comedor.backend.domain.model;

import com.comedor.backend.domain.model.enums.ProductSource;

import java.math.BigDecimal;

public class Record {
    private int id;
    private Product product;
    private BigDecimal amount;
    private ProductSource productSource;
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

    public ProductSource getProductSource() {
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

    public void setProductSource(ProductSource productSource) {
        this.productSource = productSource;
    }

    public void setUnitPrice(BigDecimal unitPrice) {
        this.unitPrice = unitPrice;
    }

    @Override
    public String toString() {
        return "Record{" +
                "id=" + id +
                ", product=" + product +
                ", amount=" + amount +
                ", productSource=" + productSource +
                ", unitPrice=" + unitPrice +
                '}';
    }
}
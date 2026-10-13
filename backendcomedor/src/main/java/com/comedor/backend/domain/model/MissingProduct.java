package com.comedor.backend.domain.model;

import java.math.BigDecimal;

public class MissingProduct {
    private final Integer productId;
    private final String productName;
    private final String productUnit;
    private final BigDecimal quantityNeeded;

    public MissingProduct(Integer productId, String productName, String productUnit, BigDecimal quantityNeeded) {
        this.productId = productId;
        this.productName = productName;
        this.productUnit = productUnit;
        this.quantityNeeded = quantityNeeded;
    }

    public Integer getProductId() {
        return productId;
    }

    public String getProductName() {
        return productName;
    }

    public String getProductUnit() {
        return productUnit;
    }

    public BigDecimal getQuantityNeeded() {
        return quantityNeeded;
    }
}
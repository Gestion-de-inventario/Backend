package com.comedor.backend.domain.model;

import java.math.BigDecimal;

public class DishSupply {

    private Integer id;

    private DishMenu dishMenu;

    private Product product;

    private BigDecimal quantityNeeded = BigDecimal.ZERO;

    public DishSupply() {
    }

    public DishSupply(Integer id,
                      DishMenu dishMenu,
                      Product product,
                      BigDecimal quantityNeeded) {

        this.id = id;
        this.dishMenu = dishMenu;
        this.product = product;
        this.quantityNeeded = quantityNeeded;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public DishMenu getDishMenu() {
        return dishMenu;
    }

    public void setDishMenu(DishMenu dishMenu) {
        this.dishMenu = dishMenu;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public BigDecimal getQuantityNeeded() {
        return quantityNeeded;
    }

    public void setQuantityNeeded(BigDecimal quantityNeeded) {
        this.quantityNeeded = quantityNeeded;
    }
}
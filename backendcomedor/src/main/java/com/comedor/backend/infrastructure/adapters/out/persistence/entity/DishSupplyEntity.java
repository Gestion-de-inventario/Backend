package com.comedor.backend.infrastructure.adapters.out.persistence.entity;

import jakarta.persistence.*;

import java.math.BigDecimal;

@Entity
@Table(name = "dish_supply")
public class DishSupplyEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "dish_supply_id")
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "dish_menu_id", nullable = false)
    private DishMenuEntity dishMenu;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private ProductEntity product;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal quantityNeeded = BigDecimal.ZERO;

    public DishSupplyEntity() {
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public DishMenuEntity getDishMenu() {
        return dishMenu;
    }

    public void setDishMenu(DishMenuEntity dishMenu) {
        this.dishMenu = dishMenu;
    }

    public ProductEntity getProduct() {
        return product;
    }

    public void setProduct(ProductEntity product) {
        this.product = product;
    }

    public BigDecimal getQuantityNeeded() {
        return quantityNeeded;
    }

    public void setQuantityNeeded(BigDecimal quantityNeeded) {
        this.quantityNeeded = quantityNeeded;
    }
}
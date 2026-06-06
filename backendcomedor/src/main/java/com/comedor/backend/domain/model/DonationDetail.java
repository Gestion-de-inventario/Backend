package com.comedor.backend.domain.model;

import java.math.BigDecimal;

public class DonationDetail {
    private Integer id;

    private Donation donation;

    private Product product;

    private BigDecimal quantity = BigDecimal.ZERO;;

    private InventoryLot inventoryLot;

    public DonationDetail() {
    }

    public Integer getId() {
        return id;
    }

    public Donation getDonation() {
        return donation;
    }

    public Product getProduct() {
        return product;
    }

    public BigDecimal getQuantity() {
        return quantity;
    }

    public InventoryLot getInventoryLot() {
        return inventoryLot;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public void setDonation(Donation donation) {
        this.donation = donation;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public void setQuantity(BigDecimal quantity) {
        this.quantity = quantity;
    }

    public void setInventoryLot(InventoryLot inventoryLot) {
        this.inventoryLot = inventoryLot;
    }
}

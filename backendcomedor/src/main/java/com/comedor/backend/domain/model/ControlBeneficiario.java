package com.comedor.backend.domain.model;

import com.comedor.backend.domain.model.enums.MetodoPago;

import java.math.BigDecimal;

public class ControlBeneficiario {
    private int id;
    private Beneficiario beneficiario;
    private boolean received = false;
    private boolean paid = false;
    private MetodoPago payMethod;
    private int menusAmount;
    private BigDecimal menuPrice;

    public void setId(int id) {
        this.id = id;
    }

    public void setBeneficiario(Beneficiario beneficiario) {
        this.beneficiario = beneficiario;
    }

    public void setReceived(boolean received) {
        this.received = received;
    }

    public void setPaid(boolean paid) {
        this.paid = paid;
    }

    public void setPayMethod(MetodoPago payMethod) {
        this.payMethod = payMethod;
    }

    public void setMenusAmount(int menusAmount) {
        this.menusAmount = menusAmount;
    }

    public void setMenuPrice(BigDecimal menuPrice) {
        this.menuPrice = menuPrice;
    }

    public int getId() {
        return id;
    }

    public Beneficiario getBeneficiario() {
        return beneficiario;
    }

    public boolean getReceived() {
        return received;
    }

    public boolean getPaid() {
        return paid;
    }

    public MetodoPago getPayMethod() {
        return payMethod;
    }

    public int getMenusAmount() {
        return menusAmount;
    }

    public BigDecimal getMenuPrice() {
        return menuPrice;
    }
}
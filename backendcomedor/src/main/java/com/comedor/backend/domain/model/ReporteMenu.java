package com.comedor.backend.domain.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class ReporteMenu {
    private int id;
    private LocalDate date;
    private List<Integer> cooks;
    private String menu;
    private List<Registro> productRecord = new ArrayList<>();;
    private List<ControlBeneficiario> beneficiariosRecord = new ArrayList<>();
    private BigDecimal totalEarned = BigDecimal.ZERO;
    private BigDecimal totalSpent = BigDecimal.ZERO;

    public void setId(int id) {
        this.id = id;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public void setCooks(List<Integer> cooks) {
        this.cooks = cooks;
    }

    public void setMenu(String menu) {
        this.menu = menu.toUpperCase();
    }

    public void setProductRecord(List<Registro> productRecord) {
        this.productRecord = productRecord;
    }

    public void setBeneficiariosRecord(List<ControlBeneficiario> beneficiariosRecord) {
        this.beneficiariosRecord = beneficiariosRecord;
    }

    public void setTotalEarned(BigDecimal totalEarned) {
        this.totalEarned = totalEarned;
    }

    public void setTotalSpent(BigDecimal totalSpent) {
        this.totalSpent = totalSpent;
    }

    public int getId() {
        return id;
    }

    public LocalDate getDate() {
        return date;
    }

    public List<Integer> getCooks() {
        return cooks;
    }

    public String getMenu() {
        return menu;
    }

    public List<Registro> getProductRecord() {
        return productRecord;
    }

    public List<ControlBeneficiario> getBeneficiariosRecord() {
        return beneficiariosRecord;
    }

    public BigDecimal getTotalEarned() {
        return totalEarned;
    }

    public BigDecimal getTotalSpent() {
        return totalSpent;
    }
}
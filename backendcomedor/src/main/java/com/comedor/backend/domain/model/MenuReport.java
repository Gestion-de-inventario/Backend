package com.comedor.backend.domain.model;

import com.comedor.backend.domain.model.enums.StatusMenuReport;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class MenuReport {
    private Integer id;
    private LocalDate date;
    private List<Integer> cooks;
    private DishMenu dishMenu;
    private Integer quantityPrepared = 0;

    private Integer quantityRemaining = 0;
    private List<StockMovement> stockMovements = new ArrayList<>();
    private List<BeneficiaryControl> beneficiaryControls = new ArrayList<>();
    private BigDecimal totalEarned = BigDecimal.ZERO;
    private BigDecimal totalSpent = BigDecimal.ZERO;

    private StatusMenuReport status;


    public MenuReport() {
    }

    public MenuReport(Integer id,
                      LocalDate date,
                      List<Integer> cooks,
                      DishMenu dishMenu,
                      Integer quantityPrepared,
                      Integer quantityRemaining,
                      List<BeneficiaryControl> beneficiaryControls,
                      List<StockMovement> stockMovements,
                      BigDecimal totalEarned,
                      BigDecimal totalSpent,
                      StatusMenuReport status) {

        this.id = id;
        this.date = date;
        this.cooks = cooks;
        this.dishMenu = dishMenu;
        this.quantityPrepared = quantityPrepared;
        this.quantityRemaining = quantityRemaining;
        this.beneficiaryControls = beneficiaryControls;
        this.stockMovements = stockMovements;
        this.totalEarned = totalEarned;
        this.totalSpent = totalSpent;
        this.status = status;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public List<Integer> getCooks() {
        return cooks;
    }

    public void setCooks(List<Integer> cooks) {
        this.cooks = cooks;
    }

    public DishMenu getDishMenu() {
        return dishMenu;
    }

    public void setDishMenu(DishMenu dishMenu) {
        this.dishMenu = dishMenu;
    }

    public Integer getQuantityPrepared() {
        return quantityPrepared;
    }

    public void setQuantityPrepared(Integer quantityPrepared) {
        this.quantityPrepared = quantityPrepared;
    }

    public Integer getQuantityRemaining() {
        return quantityRemaining;
    }

    public void setQuantityRemaining(Integer quantityRemaining) {
        this.quantityRemaining = quantityRemaining;
    }

    public List<BeneficiaryControl> getBeneficiaryControls() {
        return beneficiaryControls;
    }

    public void setBeneficiaryControls(List<BeneficiaryControl> beneficiaryControls) {
        this.beneficiaryControls = beneficiaryControls;
    }

    public List<StockMovement> getStockMovements() {
        return stockMovements;
    }

    public void setStockMovements(List<StockMovement> stockMovements) {
        this.stockMovements = stockMovements;
    }

    public BigDecimal getTotalEarned() {
        return totalEarned;
    }

    public void setTotalEarned(BigDecimal totalEarned) {
        this.totalEarned = totalEarned;
    }

    public BigDecimal getTotalSpent() {
        return totalSpent;
    }

    public void setTotalSpent(BigDecimal totalSpent) {
        this.totalSpent = totalSpent;
    }

    public StatusMenuReport getStatus() {
        return status;
    }

    public void setStatus(StatusMenuReport status) {
        this.status = status;
    }
    public void removeBeneficiaryControl(int controlId) {
        this.beneficiaryControls.removeIf(c -> c.getId() == controlId);
    }

    @Override
    public String toString() {
        return "MenuReport{" +
                "id=" + id +
                ", date=" + date +
                ", cooks=" + cooks +
                ", dishMenu=" + dishMenu +
                ", quantityPrepared=" + quantityPrepared +
                ", quantityRemaining=" + quantityRemaining +
                ", stockMovements=" + stockMovements +
                ", beneficiaryControls=" + beneficiaryControls +
                ", totalEarned=" + totalEarned +
                ", totalSpent=" + totalSpent +
                ", status=" + status +
                '}';
    }
}

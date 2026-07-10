package com.comedor.backend.domain.model;

import com.comedor.backend.domain.model.enums.TransactionReferenceType;
import com.comedor.backend.domain.model.enums.TransactionSource;
import com.comedor.backend.domain.model.enums.MovementType;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class Transactions {
    private int id;
    private User user;
    private LocalDateTime dateTime;
    private MovementType type;
    private TransactionSource source;
    private BigDecimal amount;
    private BigDecimal currentStock;
    private BigDecimal finalStock;
    private Integer referenceId;
    private TransactionReferenceType referenceType;
    private String itemName;

    public void setId(int id) {
        this.id = id;
    }

    public void setCurrentStock(BigDecimal currentStock) {
        this.currentStock = currentStock;
    }

    public void setSource(TransactionSource source) {
        this.source = source;
    }

    public void setFinalStock(BigDecimal finalStock) {
        this.finalStock = finalStock;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public void setDateTime(LocalDateTime dateTime) {
        this.dateTime = dateTime;
    }

    public void setType(MovementType type) {
        this.type = type;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public void setReferenceId(Integer referenceId) {
        this.referenceId = referenceId;
    }

    public void setReferenceType(TransactionReferenceType referenceType) {
        this.referenceType = referenceType;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public int getId() {
        return id;
    }

    public User getUser() {
        return user;
    }

    public LocalDateTime getDateTime() {
        return dateTime;
    }

    public MovementType getType() {
        return type;
    }

    public TransactionSource getSource() {
        return source;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public BigDecimal getCurrentStock() {
        return currentStock;
    }

    public BigDecimal getFinalStock() {
        return finalStock;
    }

    public Integer getReferenceId() {
        return referenceId;
    }

    public TransactionReferenceType getReferenceType() {
        return referenceType;
    }

    public String getItemName() {
        return itemName;
    }

}

package com.comedor.backend.domain.model;

import java.time.LocalDateTime;

public class PasswordResetToken {
    private Integer id;
    private String token;
    private Integer userId;
    private LocalDateTime expirationDate;
    private boolean used;

    public boolean isExpired(LocalDateTime now) {
        return expirationDate.isBefore(now);
    }

    public boolean isValid(LocalDateTime now) {
        return !used && !isExpired(now);
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public LocalDateTime getExpirationDate() {
        return expirationDate;
    }

    public void setExpirationDate(LocalDateTime expirationDate) {
        this.expirationDate = expirationDate;
    }

    public boolean isUsed() {
        return used;
    }

    public void setUsed(boolean used) {
        this.used = used;
    }
}
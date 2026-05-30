package com.comedor.backend.domain.model;

import java.time.Instant;

public class RefreshToken {

    private Integer id;
    private String token;
    private Integer userId;
    private Instant expiryDate;
    private boolean revoked;

    public RefreshToken() {
    }

    public RefreshToken(Integer id, String token, Integer userId, Instant expiryDate, boolean revoked) {
        this.id = id;
        this.token = token;
        this.userId = userId;
        this.expiryDate = expiryDate;
        this.revoked = revoked;
    }

    public Integer getId() { return id; }
    public String getToken() { return token; }
    public Integer getUserId() { return userId; }
    public Instant getExpiryDate() { return expiryDate; }
    public boolean isRevoked() { return revoked; }

    public void setId(Integer id) {
        this.id = id;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public void setExpiryDate(Instant expiryDate) {
        this.expiryDate = expiryDate;
    }

    public void setRevoked(boolean revoked) {
        this.revoked = revoked;
    }
}
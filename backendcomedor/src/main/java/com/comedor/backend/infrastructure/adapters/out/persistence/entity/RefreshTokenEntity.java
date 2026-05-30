package com.comedor.backend.infrastructure.adapters.out.persistence.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.Instant;

@Entity
@Table(name = "refresh_token")
@Data
public class RefreshTokenEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String token;

    @Column(name = "user_id")
    private Integer userId;

    private Instant expiryDate;

    private boolean revoked;
}
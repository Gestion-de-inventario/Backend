package com.comedor.backend.infrastructure.adapters.out.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "empresa_config")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class EmpresaConfigEntity {
    @Id
    @Column(name = "id")
    private Integer id = 1; // siempre 1, singleton

    @Column(nullable = false, length = 100)
    private String nombre;

    @Column(length = 200)
    private String descripcion;

    @Column(name = "logo_base64", columnDefinition = "TEXT")
    private String logoBase64;
}

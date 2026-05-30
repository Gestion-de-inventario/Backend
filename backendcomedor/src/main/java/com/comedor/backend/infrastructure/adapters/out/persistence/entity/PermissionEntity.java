package com.comedor.backend.infrastructure.adapters.out.persistence.entity;

import com.comedor.backend.domain.model.enums.ModuleCode;
import com.comedor.backend.domain.model.enums.PermissionCode;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "permission")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PermissionEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "permission_id")
    private Integer id;

    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, unique = true)
    private PermissionCode code;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ModuleCode module;
}
package com.comedor.backend.infrastructure.adapters.out.persistence.entity;

import com.comedor.backend.domain.model.enums.Estado;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "usuario")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UsuarioEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Integer id;

    @Column(nullable = false, unique = true, length = 8)
    private String username;

    @Column(nullable = false, length = 20)
    private String password;

    @ManyToOne(optional = false)
    @JoinColumn(name = "rol_id", nullable = false)
    private RolEntity role;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Estado status = Estado.ACTIVO;

    @OneToOne(mappedBy = "user",cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private PersonaEntity persona;
}
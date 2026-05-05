package com.comedor.backend.infrastructure.adapters.out.persistence.entity;

import com.comedor.backend.domain.model.enums.Estado;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

@Entity
@Table(name = "producto")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductoEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "producto_id")
    private int id;

    private String name;

    @ManyToOne(optional = false)
    @JoinColumn(name = "categoria_id", nullable = false)
    private CategoriaEntity categoria;

    @ManyToOne(optional = true)
    @JoinColumn(name = "etiqueta_id", nullable = true)
    private EtiquetaEntity etiqueta;

    private String unit;

    @Enumerated(EnumType.STRING)
    private Estado status;

    @Column(precision = 10, scale = 3)
    private BigDecimal stock;

    @Column(name = "reorder_point",precision = 10, scale = 3)
    private BigDecimal reorderPoint;
}
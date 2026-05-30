package com.comedor.backend.infrastructure.adapters.out.persistence.entity;

import com.comedor.backend.domain.model.enums.Estado;
import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "dish_menu")
public class DishMenuEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "dish_menu_id")
    private Integer id;

    @Column(nullable = false, unique = true, length = 100)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Estado status = Estado.ACTIVO;

    @OneToMany(mappedBy = "dishMenu", cascade = CascadeType.ALL)
    private List<DishSupplyEntity> supplies = new ArrayList<>();

    public DishMenuEntity() {
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Estado getStatus() {
        return status;
    }

    public void setStatus(Estado status) {
        this.status = status;
    }

    public List<DishSupplyEntity> getSupplies() {
        return supplies;
    }

    public void setSupplies(List<DishSupplyEntity> supplies) {
        this.supplies = supplies;
    }
}
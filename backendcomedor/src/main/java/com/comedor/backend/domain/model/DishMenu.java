package com.comedor.backend.domain.model;

import com.comedor.backend.domain.model.enums.Estado;

import java.util.ArrayList;
import java.util.List;

public class DishMenu {

    private Integer id;

    private String name;

    private Estado status = Estado.ACTIVO;

    private List<DishSupply> supplies = new ArrayList<>();

    public DishMenu() {
    }

    public DishMenu(Integer id,
                    String name,
                    Estado status,
                    List<DishSupply> supplies) {

        this.id = id;
        this.name = name;
        this.status = status;
        this.supplies = supplies;
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

    public List<DishSupply> getSupplies() {
        return supplies;
    }

    public void setSupplies(List<DishSupply> supplies) {
        this.supplies = supplies;
    }

    @Override
    public String toString() {
        return "DishMenu{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", status=" + status +
                ", supplies=" + supplies +
                '}';
    }
}
package com.comedor.backend.domain.model;

import com.comedor.backend.domain.model.enums.Status;

import java.math.BigDecimal;

public class BeneficiaryType {
    private Integer id;
    private String name;
    private String desc;
    private BigDecimal menu_cost;
    private Status status = Status.ACTIVO;

    public BeneficiaryType() {
    }

    public BeneficiaryType(Integer id, String name, String desc, BigDecimal menu_cost, Status status) {
        this.id = id;
        this.name = name;
        this.desc = desc;
        this.menu_cost = menu_cost;
        this.status = status;
    }

    public Integer getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDesc() {
        return desc;
    }

    public BigDecimal getMenu_cost() {
        return menu_cost;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public void setMenu_cost(BigDecimal menu_cost) {
        this.menu_cost = menu_cost;
    }

    public void setName(String name) {
        this.name = name.toUpperCase();
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "BeneficiaryType{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", desc='" + desc + '\'' +
                ", menu_cost=" + menu_cost +
                ", status=" + status +
                '}';
    }
}

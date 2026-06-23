package com.comedor.backend.domain.model;

import com.comedor.backend.domain.model.enums.Status;

public class Category {
    private int id;
    private String name;
    private Status status = Status.ACTIVO;

    public void setStatus(Status status) {
        this.status = status;
    }

    public void setName(String name){
        this.name = name.toUpperCase();
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }
    public String getName() {
        return name;
    }

    public Status getStatus() {
        return status;
    }
}

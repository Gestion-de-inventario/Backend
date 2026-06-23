package com.comedor.backend.domain.model;
import com.comedor.backend.domain.model.enums.Status;

public class Tag {
    private int id;
    private String name;
    private Status status = Status.ACTIVO;

    public void setName(String name) {
        this.name = name.toUpperCase();
    }
    public void setId(int id) {
        this.id = id;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public String getName() {
        return name;
    }
    public Status getStatus() {
        return status;
    }
    public int getId() {
        return id;
    }

}

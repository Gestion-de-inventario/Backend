package com.comedor.backend.domain.model;
import com.comedor.backend.domain.model.enums.Estado;

public class Tag {
    private int id;
    private String name;
    private Estado status = Estado.ACTIVO;

    public void setName(String name) {
        this.name = name.toUpperCase();
    }
    public void setId(int id) {
        this.id = id;
    }

    public void setStatus(Estado status) {
        this.status = status;
    }

    public String getName() {
        return name;
    }
    public Estado getStatus() {
        return status;
    }
    public int getId() {
        return id;
    }

}

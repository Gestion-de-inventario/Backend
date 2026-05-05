package com.comedor.backend.domain.model;
import com.comedor.backend.domain.model.enums.Estado;

public class Etiqueta {
    private int id;
    private String name;
    private Estado status = Estado.ACTIVO;

    public void setName(String name) {
        this.name = name.toUpperCase();
    }

    public String getName() {
        return name;
    }

    public void setStatus(Estado status) {
        this.status = status;
    }

    public Estado getStatus() {
        return status;
    }
}

package com.comedor.backend.infrastructure.adapters.in.web.dto.request;

import java.math.BigDecimal;

public class ProductRotationDTO {
    private String nombre;
    private String unidad;
    private BigDecimal totalMovido;

    public ProductRotationDTO(String nombre, String unidad, BigDecimal totalMovido) {
        this.nombre = nombre;
        this.unidad = unidad;
        this.totalMovido = totalMovido;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getUnidad() {
        return unidad;
    }

    public void setUnidad(String unidad) {
        this.unidad = unidad;
    }

    public BigDecimal getTotalMovido() {
        return totalMovido;
    }

    public void setTotalMovido(BigDecimal totalMovido) {
        this.totalMovido = totalMovido;
    }
}

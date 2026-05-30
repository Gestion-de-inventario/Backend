package com.comedor.backend.domain.model.enums;

public enum CambioEstado {

    ACTIVAR(Estado.ACTIVO),
    DESACTIVAR(Estado.INACTIVO);

    private final Estado estado;

    CambioEstado(Estado estado) {
        this.estado = estado;
    }

    public Estado toEstado() {
        return estado;
    }
}
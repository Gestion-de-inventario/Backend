package com.comedor.backend.domain.model.enums;

public enum ChangeStatus {

    ACTIVAR(Status.ACTIVO),
    DESACTIVAR(Status.INACTIVO);

    private final Status status;

    ChangeStatus(Status status) {
        this.status = status;
    }

    public Status toEstado() {
        return status;
    }
}
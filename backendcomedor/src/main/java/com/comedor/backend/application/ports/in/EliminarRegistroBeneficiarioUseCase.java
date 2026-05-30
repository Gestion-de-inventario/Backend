package com.comedor.backend.application.ports.in;

public interface EliminarRegistroBeneficiarioUseCase {
    void eliminarRegistroBeneficiario(
            int reporteId,
            int controlId
    );
}

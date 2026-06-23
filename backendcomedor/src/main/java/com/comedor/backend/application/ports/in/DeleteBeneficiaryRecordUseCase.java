package com.comedor.backend.application.ports.in;

public interface DeleteBeneficiaryRecordUseCase {
    void eliminarRegistroBeneficiario(
            int reporteId,
            int controlId
    );
}

package com.comedor.backend.application.ports.out;

import com.comedor.backend.domain.model.BeneficiaryControl;

import java.util.List;

public interface BeneficiaryControlRepositoryPort {
    BeneficiaryControl agregarBeneficiario(int reporteId, BeneficiaryControl control);

    BeneficiaryControl actualizarBeneficiario(int reporteId, int controlId, BeneficiaryControl control);

    void eliminarBeneficiario(int reporteId,int controlId);

    BeneficiaryControl findById(int controlId);

    List<BeneficiaryControl> findByReporteId(int reporteId);
}

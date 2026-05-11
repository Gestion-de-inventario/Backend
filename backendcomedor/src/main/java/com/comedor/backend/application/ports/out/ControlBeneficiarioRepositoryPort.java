package com.comedor.backend.application.ports.out;

import com.comedor.backend.domain.model.ControlBeneficiario;

import java.util.List;

public interface ControlBeneficiarioRepositoryPort {
    ControlBeneficiario agregarBeneficiario(int reporteId,ControlBeneficiario control);

    ControlBeneficiario actualizarBeneficiario(int reporteId,int controlId,ControlBeneficiario control);

    void eliminarBeneficiario(int reporteId,int controlId);

    ControlBeneficiario findById( int controlId);

    List<ControlBeneficiario> findByReporteId(int reporteId);
}

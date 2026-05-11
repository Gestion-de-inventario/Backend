package com.comedor.backend.application.services;

import com.comedor.backend.application.common.mapper.ControlBeneficiarioMapper;
import com.comedor.backend.application.ports.in.EditarRegistroBeneficiarioUseCase;
import com.comedor.backend.application.ports.in.RecalcularResumenReporteUseCase;
import com.comedor.backend.application.ports.out.ControlBeneficiarioRepositoryPort;
import com.comedor.backend.domain.model.ControlBeneficiario;
import com.comedor.backend.infrastructure.adapters.in.web.dto.request.ControlBeneficiarioRequestDTO;
import com.comedor.backend.infrastructure.adapters.in.web.dto.response.RegistroBeneficiarioResponseDTO;
import jakarta.transaction.Transactional;

@Transactional
public class EditarRegistroBeneficiarioService implements EditarRegistroBeneficiarioUseCase {
    private final ControlBeneficiarioRepositoryPort controlBeneficiarioRepositoryPort;
    private final ControlBeneficiarioMapper controlBeneficiarioMapper;
    private final RecalcularResumenReporteUseCase
            recalcularResumenReporteUseCase;
    public EditarRegistroBeneficiarioService(ControlBeneficiarioRepositoryPort controlBeneficiarioRepositoryPort, ControlBeneficiarioMapper controlBeneficiarioMapper, RecalcularResumenReporteUseCase recalcularResumenReporteUseCase) {
        this.controlBeneficiarioRepositoryPort = controlBeneficiarioRepositoryPort;
        this.controlBeneficiarioMapper = controlBeneficiarioMapper;
        this.recalcularResumenReporteUseCase = recalcularResumenReporteUseCase;
    }

    @Override
    public RegistroBeneficiarioResponseDTO editarRegistroBeneficiario(int reporteId,int controlId, ControlBeneficiarioRequestDTO dto) {

        ControlBeneficiario actual =
                controlBeneficiarioRepositoryPort
                        .findById(controlId);


        if(dto.getPago() != null)
        {
            actual.setPaid(dto.getPago());
        }

        if(dto.getEntregado() != null)
        {
            actual.setReceived(dto.getEntregado());
        }

        if(dto.getPayMethod() != null)
        {
            actual.setPayMethod(dto.getPayMethod());
        }

        if(dto.getMenusAmount() != null)
        {
            actual.setMenusAmount(dto.getMenusAmount());
        }

        if(dto.getMenuPrice() != null)
        {
            actual.setMenuPrice(dto.getMenuPrice());
        }

        ControlBeneficiario actualizado =
                controlBeneficiarioRepositoryPort
                        .actualizarBeneficiario(
                                reporteId,
                                controlId,
                                actual
                        );

        recalcularResumenReporteUseCase.recalcular(reporteId);

        return controlBeneficiarioMapper
                .toDto(actualizado);
    }

}

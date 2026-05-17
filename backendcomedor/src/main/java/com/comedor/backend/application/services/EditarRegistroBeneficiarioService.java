package com.comedor.backend.application.services;

import com.comedor.backend.application.common.mapper.BeneficiaryControlMapper;
import com.comedor.backend.application.ports.in.EditarRegistroBeneficiarioUseCase;
import com.comedor.backend.application.ports.in.RecalcularResumenReporteUseCase;
import com.comedor.backend.application.ports.out.BeneficiaryControlRepositoryPort;
import com.comedor.backend.domain.model.BeneficiaryControl;
import com.comedor.backend.infrastructure.adapters.in.web.dto.request.ControlBeneficiarioRequestDTO;
import com.comedor.backend.infrastructure.adapters.in.web.dto.response.RegistroBeneficiarioResponseDTO;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public class EditarRegistroBeneficiarioService implements EditarRegistroBeneficiarioUseCase {
    private final BeneficiaryControlRepositoryPort beneficiaryControlRepositoryPort;
    private final BeneficiaryControlMapper beneficiaryControlMapper;
    private final RecalcularResumenReporteUseCase
            recalcularResumenReporteUseCase;
    public EditarRegistroBeneficiarioService(BeneficiaryControlRepositoryPort beneficiaryControlRepositoryPort, BeneficiaryControlMapper beneficiaryControlMapper, RecalcularResumenReporteUseCase recalcularResumenReporteUseCase) {
        this.beneficiaryControlRepositoryPort = beneficiaryControlRepositoryPort;
        this.beneficiaryControlMapper = beneficiaryControlMapper;
        this.recalcularResumenReporteUseCase = recalcularResumenReporteUseCase;
    }

    @Override
    public RegistroBeneficiarioResponseDTO editarRegistroBeneficiario(int reporteId,int controlId, ControlBeneficiarioRequestDTO dto) {

        BeneficiaryControl actual =
                beneficiaryControlRepositoryPort
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

        BeneficiaryControl actualizado =
                beneficiaryControlRepositoryPort
                        .actualizarBeneficiario(
                                reporteId,
                                controlId,
                                actual
                        );

        recalcularResumenReporteUseCase.recalcular(reporteId);

        return beneficiaryControlMapper
                .toDto(actualizado);
    }

}

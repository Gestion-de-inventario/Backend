package com.comedor.backend.application.services;

import com.comedor.backend.application.common.mapper.BeneficiaryControlMapper;
import com.comedor.backend.application.ports.in.EditarRegistroBeneficiarioUseCase;
import com.comedor.backend.application.ports.in.RecalcularResumenReporteUseCase;
import com.comedor.backend.application.ports.out.BeneficiaryControlRepositoryPort;
import com.comedor.backend.application.ports.out.MenuReportRepositoryPort;
import com.comedor.backend.domain.model.BeneficiaryControl;
import com.comedor.backend.domain.model.MenuReport;
import com.comedor.backend.infrastructure.adapters.in.web.dto.request.ControlBeneficiarioRequestDTO;
import com.comedor.backend.infrastructure.adapters.in.web.dto.response.RegistroBeneficiarioResponseDTO;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public class EditarRegistroBeneficiarioService implements EditarRegistroBeneficiarioUseCase {
    private final BeneficiaryControlRepositoryPort beneficiaryControlRepositoryPort;
    private final BeneficiaryControlMapper beneficiaryControlMapper;
    private final RecalcularResumenReporteUseCase recalcularResumenReporteUseCase;
    private final MenuReportRepositoryPort menuReportRepositoryPort;
    public EditarRegistroBeneficiarioService(BeneficiaryControlRepositoryPort beneficiaryControlRepositoryPort, BeneficiaryControlMapper beneficiaryControlMapper, RecalcularResumenReporteUseCase recalcularResumenReporteUseCase, MenuReportRepositoryPort menuReportRepositoryPort) {
        this.beneficiaryControlRepositoryPort = beneficiaryControlRepositoryPort;
        this.beneficiaryControlMapper = beneficiaryControlMapper;
        this.recalcularResumenReporteUseCase = recalcularResumenReporteUseCase;
        this.menuReportRepositoryPort = menuReportRepositoryPort;
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

        if (dto.getMenusAmount() != null) {

            int oldAmount = actual.getMenusAmount();
            int newAmount = dto.getMenusAmount();

            int difference = newAmount - oldAmount;

            MenuReport report =
                    menuReportRepositoryPort.findById(reporteId);

            if (difference > 0) {

                if (report.getQuantityRemaining() < difference) {
                    throw new RuntimeException(
                            "Solo quedan " + report.getQuantityRemaining()
                                    + " menus disponibles"
                    );
                }

                report.setQuantityRemaining(
                        report.getQuantityRemaining() - difference
                );
            }

            if (difference < 0) {
                report.setQuantityRemaining(
                        report.getQuantityRemaining() + Math.abs(difference)
                );
            }

            actual.setMenusAmount(newAmount);
            menuReportRepositoryPort.update(report);
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

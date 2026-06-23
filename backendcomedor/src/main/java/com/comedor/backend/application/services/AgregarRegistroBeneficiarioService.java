package com.comedor.backend.application.services;


import com.comedor.backend.application.common.mapper.BeneficiaryControlMapper;
import com.comedor.backend.application.ports.in.AgregarRegistroBeneficiarioUseCase;
import com.comedor.backend.application.ports.in.RecalcularResumenReporteUseCase;
import com.comedor.backend.application.ports.out.BeneficiaryControlRepositoryPort;
import com.comedor.backend.application.ports.out.BeneficiaryRepositoryPort;
import com.comedor.backend.application.ports.out.MenuReportRepositoryPort;
import com.comedor.backend.application.ports.out.ProductRecordRepositoryPort;
import com.comedor.backend.domain.exceptions.BeneficiarioNoEncontradoException;
import com.comedor.backend.domain.exceptions.CantidadMenuInvalidad;
import com.comedor.backend.domain.exceptions.PrecioMenuInvalido;
import com.comedor.backend.domain.model.Beneficiary;
import com.comedor.backend.domain.model.BeneficiaryControl;
import com.comedor.backend.domain.model.MenuReport;
import com.comedor.backend.infrastructure.adapters.in.web.dto.request.ControlBeneficiarioRequestDTO;
import com.comedor.backend.infrastructure.adapters.in.web.dto.response.RegistroBeneficiarioResponseDTO;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;


public class AgregarRegistroBeneficiarioService implements AgregarRegistroBeneficiarioUseCase {

    private final BeneficiaryControlRepositoryPort beneficiaryControlRepositoryPort;

    private final BeneficiaryControlMapper beneficiaryControlMapper;

    private final BeneficiaryRepositoryPort beneficiaryRepositoryPort;

    private final RecalcularResumenReporteUseCase recalcularResumenReporteUseCase;

    private final MenuReportRepositoryPort menuReportRepositoryPort;

    public AgregarRegistroBeneficiarioService(
            BeneficiaryControlRepositoryPort beneficiaryControlRepositoryPort,
            BeneficiaryControlMapper beneficiaryControlMapper, BeneficiaryRepositoryPort beneficiaryRepositoryPort,
            RecalcularResumenReporteUseCase recalcularResumenReporteUseCase, MenuReportRepositoryPort menuReportRepositoryPort
    ) {
        this.beneficiaryControlRepositoryPort =
                beneficiaryControlRepositoryPort;

        this.beneficiaryControlMapper =
                beneficiaryControlMapper;
        this.beneficiaryRepositoryPort = beneficiaryRepositoryPort;

        this.recalcularResumenReporteUseCase =
                recalcularResumenReporteUseCase;
        this.menuReportRepositoryPort = menuReportRepositoryPort;
    }

    @Override
    @Transactional
    public RegistroBeneficiarioResponseDTO
    agregarRegistroBeneficiario(
            int reporteId,
            ControlBeneficiarioRequestDTO dto
    ) {

        validar(dto);

        MenuReport report =
                menuReportRepositoryPort.findById(reporteId);

        if (report.getQuantityRemaining() < dto.getMenusAmount()) {
            throw new RuntimeException(
                    "No hay suficientes menus disponibles. Quedan: "
                            + report.getQuantityRemaining()
            );
        }

        BeneficiaryControl control =
                beneficiaryControlMapper
                        .toDomain(dto);
        Beneficiary beneficiary = beneficiaryRepositoryPort.findById(dto.getBeneficiarioId()).orElseThrow(()-> new BeneficiarioNoEncontradoException("Beneficiario no encontrado"));

        control.setBeneficiario(beneficiary);

        BeneficiaryControl creado =
                beneficiaryControlRepositoryPort
                        .agregarBeneficiario(
                                reporteId,
                                control
                        );

        report.setQuantityRemaining(
                report.getQuantityRemaining() - dto.getMenusAmount()
        );

        menuReportRepositoryPort.update(report);

        recalcularResumenReporteUseCase
                .recalcular(reporteId);

        return beneficiaryControlMapper
                .toDto(creado);
    }

    private void validar(
            ControlBeneficiarioRequestDTO dto
    ) {

        if(dto.getMenusAmount() == null){
            throw new CantidadMenuInvalidad(
                    "La cantidad de menus es obligatoria"
            );
        }

        if(dto.getMenusAmount() <= 0){
            throw new CantidadMenuInvalidad(
                    "La cantidad de menus debe ser mayor a 0"
            );
        }

        if(dto.getMenuPrice() == null){
            throw new PrecioMenuInvalido(
                    "El precio del menu es obligatorio"
            );
        }

        if(dto.getMenuPrice()
                .compareTo(BigDecimal.ZERO) < 0){

            throw new PrecioMenuInvalido(
                    "El precio no puede ser negativo"
            );
        }
    }
}

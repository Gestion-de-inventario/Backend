package com.comedor.backend.application.services;


import com.comedor.backend.application.common.mapper.BeneficiaryControlMapper;
import com.comedor.backend.application.ports.in.AgregarRegistroBeneficiarioUseCase;
import com.comedor.backend.application.ports.in.RecalcularResumenReporteUseCase;
import com.comedor.backend.application.ports.out.BeneficiaryControlRepositoryPort;
import com.comedor.backend.domain.exceptions.CantidadMenuInvalidad;
import com.comedor.backend.domain.exceptions.PrecioMenuInvalido;
import com.comedor.backend.domain.model.BeneficiaryControl;
import com.comedor.backend.infrastructure.adapters.in.web.dto.request.ControlBeneficiarioRequestDTO;
import com.comedor.backend.infrastructure.adapters.in.web.dto.response.RegistroBeneficiarioResponseDTO;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;


public class AgregarRegistroBeneficiarioService implements AgregarRegistroBeneficiarioUseCase {

    private final BeneficiaryControlRepositoryPort beneficiaryControlRepositoryPort;

    private final BeneficiaryControlMapper beneficiaryControlMapper;

    private final RecalcularResumenReporteUseCase recalcularResumenReporteUseCase;

    public AgregarRegistroBeneficiarioService(
            BeneficiaryControlRepositoryPort beneficiaryControlRepositoryPort,
            BeneficiaryControlMapper beneficiaryControlMapper,
            RecalcularResumenReporteUseCase recalcularResumenReporteUseCase
    ) {
        this.beneficiaryControlRepositoryPort =
                beneficiaryControlRepositoryPort;

        this.beneficiaryControlMapper =
                beneficiaryControlMapper;

        this.recalcularResumenReporteUseCase =
                recalcularResumenReporteUseCase;
    }

    @Override
    @Transactional
    public RegistroBeneficiarioResponseDTO
    agregarRegistroBeneficiario(
            int reporteId,
            ControlBeneficiarioRequestDTO dto
    ) {

        validar(dto);

        BeneficiaryControl control =
                beneficiaryControlMapper
                        .toDomain(dto);

        BeneficiaryControl creado =
                beneficiaryControlRepositoryPort
                        .agregarBeneficiario(
                                reporteId,
                                control
                        );

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

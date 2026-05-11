package com.comedor.backend.application.common.mapper;

import com.comedor.backend.domain.model.Beneficiario;
import com.comedor.backend.domain.model.ControlBeneficiario;
import com.comedor.backend.infrastructure.adapters.in.web.dto.request.ControlBeneficiarioRequestDTO;
import com.comedor.backend.infrastructure.adapters.in.web.dto.response.RegistroBeneficiarioResponseDTO;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class ControlBeneficiarioMapper {
    public ControlBeneficiario toDomain(ControlBeneficiarioRequestDTO dto) {

        ControlBeneficiario control = new ControlBeneficiario();

        Beneficiario beneficiario = new Beneficiario();

        beneficiario.setId(dto.getBeneficiarioId());

        control.setBeneficiario(beneficiario);

        if (dto.getPago() != null) {
            control.setPaid(dto.getPago());
        }

        if (dto.getEntregado() != null) {
            control.setReceived(dto.getEntregado());
        }
        if(dto.getPayMethod() != null) {
            control.setPayMethod(dto.getPayMethod());
        }

        if (dto.getMenusAmount() != null) {
            control.setMenusAmount(dto.getMenusAmount());
        }
        if(dto.getMenuPrice() != null)
        {control.setMenuPrice(dto.getMenuPrice());
        }

        return control;
    }

    public RegistroBeneficiarioResponseDTO toDto(
            ControlBeneficiario control
    ) {

        RegistroBeneficiarioResponseDTO dto =
                new RegistroBeneficiarioResponseDTO();

        dto.setName(
                control.getBeneficiario().getName()
        );

        dto.setLastName(
                control.getBeneficiario().getLastname()
        );

        dto.setCantidad(control.getMenusAmount());

        dto.setMetodoPago(
                control.getPayMethod()
        );

        dto.setPago(
                control.getPaid()
        );

        dto.setEntregado(
                control.getReceived()
        );

        dto.setTotal(
                control.getMenuPrice().multiply(
                        BigDecimal.valueOf(
                                control.getMenusAmount()
                        )
                )
        );

        return dto;
    }
}

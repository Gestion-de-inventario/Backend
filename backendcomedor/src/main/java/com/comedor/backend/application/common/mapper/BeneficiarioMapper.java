package com.comedor.backend.application.common.mapper;

import com.comedor.backend.domain.model.Beneficiario;
import com.comedor.backend.infrastructure.adapters.in.web.dto.request.BeneficiarioRequestDTO;
import com.comedor.backend.infrastructure.adapters.in.web.dto.response.BeneficiarioResponseDTO;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@AllArgsConstructor
public class BeneficiarioMapper {

    private final ModelMapper modelMapper;

    public Beneficiario convertToDomain(BeneficiarioRequestDTO requestDTO) {
        return modelMapper.map(requestDTO, Beneficiario.class);
    }

    public BeneficiarioResponseDTO convertToDTO(Beneficiario beneficiario) {
        return modelMapper.map(beneficiario, BeneficiarioResponseDTO.class);
    }

    public List<BeneficiarioResponseDTO> convertToListDTO(List<Beneficiario> beneficiarios) {
        return beneficiarios.stream()
                .map(this::convertToDTO)
                .toList();
    }
}

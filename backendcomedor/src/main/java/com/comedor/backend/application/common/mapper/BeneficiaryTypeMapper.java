package com.comedor.backend.application.common.mapper;

import com.comedor.backend.domain.model.BeneficiaryType;
import com.comedor.backend.infrastructure.adapters.in.web.dto.request.BeneficiaryTypeRequestDTO;
import com.comedor.backend.infrastructure.adapters.in.web.dto.response.BeneficiaryTypeResponseDTO;
import org.springframework.stereotype.Component;

import java.util.List;
@Component
public class BeneficiaryTypeMapper {

    public BeneficiaryType convertToDomain(BeneficiaryTypeRequestDTO requestDTO) {
        BeneficiaryType beneficiaryType = new BeneficiaryType();

        beneficiaryType.setName(requestDTO.getName());
        beneficiaryType.setDesc(requestDTO.getDesc());
        beneficiaryType.setMenu_cost(requestDTO.getMenu_cost());

        return beneficiaryType;
    }

    public BeneficiaryTypeResponseDTO convertToDTO(BeneficiaryType domain) {
        BeneficiaryTypeResponseDTO beneficiarioResponseDTO = new BeneficiaryTypeResponseDTO();

        beneficiarioResponseDTO.setId(domain.getId());
        beneficiarioResponseDTO.setName(domain.getName());
        beneficiarioResponseDTO.setDesc(domain.getDesc());
        beneficiarioResponseDTO.setMenu_cost(domain.getMenu_cost());
        beneficiarioResponseDTO.setStatus(domain.getStatus());
        return beneficiarioResponseDTO;
    }

    public List<BeneficiaryTypeResponseDTO> convertToListDTO(List<BeneficiaryType> beneficiariesTypes) {
        return beneficiariesTypes.stream()
                .map(this::convertToDTO)
                .toList();
    }
}

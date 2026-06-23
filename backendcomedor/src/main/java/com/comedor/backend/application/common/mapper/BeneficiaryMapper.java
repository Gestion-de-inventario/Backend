package com.comedor.backend.application.common.mapper;

import com.comedor.backend.domain.model.Beneficiary;
import com.comedor.backend.domain.model.BeneficiaryType;
import com.comedor.backend.domain.model.PersonalDataReniec;
import com.comedor.backend.domain.model.enums.Status;
import com.comedor.backend.infrastructure.adapters.in.web.dto.request.BeneficiaryRequestDTO;
import com.comedor.backend.infrastructure.adapters.in.web.dto.response.BeneficiaryResponseDTO;
import com.comedor.backend.infrastructure.adapters.in.web.dto.response.PersonalDataResponseDTO;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class BeneficiaryMapper {

    public Beneficiary convertToDomain(BeneficiaryRequestDTO requestDTO) {
        BeneficiaryType type = new BeneficiaryType();
        type.setId(requestDTO.getBeneficiaryTypeId());
        Beneficiary domain = new  Beneficiary(
                0,
                requestDTO.getDni(),
                requestDTO.getName(),
                requestDTO.getLastname(),
                Status.ACTIVO,
                type

       );

        return domain;
    }

    public BeneficiaryResponseDTO convertToDTO(Beneficiary beneficiary) {
        BeneficiaryResponseDTO beneficiaryResponseDTO = new BeneficiaryResponseDTO();

        beneficiaryResponseDTO.setId(beneficiary.getId());
        beneficiaryResponseDTO.setDni(beneficiary.getDni());
        beneficiaryResponseDTO.setName(beneficiary.getName());
        beneficiaryResponseDTO.setLastname(beneficiary.getLastname());
        beneficiaryResponseDTO.setStatus(beneficiary.getStatus());
        beneficiaryResponseDTO.setBeneficiaryType(beneficiary.getBeneficiaryType().getName());
        beneficiaryResponseDTO.setBeneficiaryTypeId(beneficiary.getBeneficiaryType().getId());
        beneficiaryResponseDTO.setMenu_cost(beneficiary.getBeneficiaryType().getMenu_cost());
        return beneficiaryResponseDTO;
    }

    public List<BeneficiaryResponseDTO> convertToListDTO(List<Beneficiary> beneficiaries) {
        return beneficiaries.stream()
                .map(this::convertToDTO)
                .toList();
    }

    public PersonalDataResponseDTO convertDatosPersonalesToDTO(PersonalDataReniec personalDataReniec) {
        return new PersonalDataResponseDTO(
                personalDataReniec.getDni(),
                personalDataReniec.getNames(),
                personalDataReniec.getLastnames()
        );
    }
}

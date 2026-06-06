package com.comedor.backend.application.common.mapper;

import com.comedor.backend.domain.model.Beneficiary;
import com.comedor.backend.domain.model.BeneficiaryType;
import com.comedor.backend.domain.model.PersonalDataReniec;
import com.comedor.backend.domain.model.enums.Estado;
import com.comedor.backend.infrastructure.adapters.in.web.dto.request.BeneficiarioRequestDTO;
import com.comedor.backend.infrastructure.adapters.in.web.dto.request.EditarBeneficiarioRequestDTO;
import com.comedor.backend.infrastructure.adapters.in.web.dto.response.BeneficiarioResponseDTO;
import com.comedor.backend.infrastructure.adapters.in.web.dto.response.DatosPersonalesResponseDTO;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class BeneficiaryMapper {

    public Beneficiary convertToDomain(BeneficiarioRequestDTO requestDTO) {
        BeneficiaryType type = new BeneficiaryType();
        type.setId(requestDTO.getBeneficiaryTypeId());
        Beneficiary domain = new  Beneficiary(
                0,
                requestDTO.getDni(),
                requestDTO.getName(),
                requestDTO.getLastname(),
                Estado.ACTIVO,
                type

       );

        return domain;
    }

    public BeneficiarioResponseDTO convertToDTO(Beneficiary beneficiary) {
        BeneficiarioResponseDTO beneficiarioResponseDTO = new BeneficiarioResponseDTO();

        beneficiarioResponseDTO.setId(beneficiary.getId());
        beneficiarioResponseDTO.setDni(beneficiary.getDni());
        beneficiarioResponseDTO.setName(beneficiary.getName());
        beneficiarioResponseDTO.setLastname(beneficiary.getLastname());
        beneficiarioResponseDTO.setStatus(beneficiary.getStatus());
        beneficiarioResponseDTO.setMenu_cost(beneficiary.getBeneficiaryType().getMenu_cost());
        return beneficiarioResponseDTO;
    }

    public List<BeneficiarioResponseDTO> convertToListDTO(List<Beneficiary> beneficiaries) {
        return beneficiaries.stream()
                .map(this::convertToDTO)
                .toList();
    }

    public DatosPersonalesResponseDTO convertDatosPersonalesToDTO(PersonalDataReniec personalDataReniec) {
        return new DatosPersonalesResponseDTO(
                personalDataReniec.getDni(),
                personalDataReniec.getNames(),
                personalDataReniec.getLastnames()
        );
    }

    public Beneficiary convertToDomainUpdate(EditarBeneficiarioRequestDTO editarBeneficiarioRequest, int id) {
        BeneficiaryType type = new BeneficiaryType();
        type.setId(editarBeneficiarioRequest.getBeneficiaryTypeId());

        return new Beneficiary(
                id,
                editarBeneficiarioRequest.getDni(),
                editarBeneficiarioRequest.getName(),
                editarBeneficiarioRequest.getLastname(),
                editarBeneficiarioRequest.getStatus(),
                type);
    }

}

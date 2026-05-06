package com.comedor.backend.application.common.mapper;

import com.comedor.backend.domain.model.Beneficiario;
import com.comedor.backend.domain.model.DatosPersonales;
import com.comedor.backend.domain.model.enums.Estado;
import com.comedor.backend.infrastructure.adapters.in.web.dto.request.BeneficiarioRequestDTO;
import com.comedor.backend.infrastructure.adapters.in.web.dto.request.EditarBeneficiarioRequest;
import com.comedor.backend.infrastructure.adapters.in.web.dto.response.BeneficiarioResponseDTO;
import com.comedor.backend.infrastructure.adapters.in.web.dto.response.DatosPersonalesResponseDTO;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class BeneficiarioMapper {

    public Beneficiario convertToDomain(BeneficiarioRequestDTO requestDTO) {
       return new Beneficiario(
               0,
               requestDTO.getDni(),
               requestDTO.getName(),
               requestDTO.getLastname(),
               Estado.ACTIVO
       );
    }

    public BeneficiarioResponseDTO convertToDTO(Beneficiario beneficiario) {
        BeneficiarioResponseDTO beneficiarioResponseDTO = new BeneficiarioResponseDTO();

        beneficiarioResponseDTO.setId(beneficiario.getId());
        beneficiarioResponseDTO.setDni(beneficiario.getDni());
        beneficiarioResponseDTO.setName(beneficiario.getName());
        beneficiarioResponseDTO.setLastname(beneficiario.getLastname());
        beneficiarioResponseDTO.setStatus(beneficiario.getStatus());
        return beneficiarioResponseDTO;
    }

    public List<BeneficiarioResponseDTO> convertToListDTO(List<Beneficiario> beneficiarios) {
        return beneficiarios.stream()
                .map(this::convertToDTO)
                .toList();
    }

    public DatosPersonalesResponseDTO convertDatosPersonalesToDTO(DatosPersonales datosPersonales) {
        return new DatosPersonalesResponseDTO(
                datosPersonales.getDni(),
                datosPersonales.getNames(),
                datosPersonales.getLastnames()
        );
    }

    public Beneficiario convertToDomainUpdate(EditarBeneficiarioRequest editarBeneficiarioRequest, int id) {
        return new Beneficiario(
                id,
                editarBeneficiarioRequest.getDni(),
                editarBeneficiarioRequest.getName(),
                editarBeneficiarioRequest.getLastName(),
                editarBeneficiarioRequest.getStatus());
    }

}

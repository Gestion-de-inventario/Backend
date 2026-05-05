package com.comedor.backend.application.common.mapper;

import com.comedor.backend.domain.model.Etiqueta;
import com.comedor.backend.infrastructure.adapters.in.web.dto.request.EtiquetaRequestDTO;
import com.comedor.backend.infrastructure.adapters.in.web.dto.response.EtiquetaResponseDTO;
import org.springframework.stereotype.Component;

import java.util.List;
@Component
public class EtiquetaMapper {

    public Etiqueta toDomain(EtiquetaRequestDTO etiquetaRequestDTO)
    {
        if (etiquetaRequestDTO == null)
            return null;
        Etiqueta etiqueta = new Etiqueta();
        etiqueta.setName(etiquetaRequestDTO.getName());
        return etiqueta;
    }

    public EtiquetaResponseDTO toEtiquetaResponseDTO (Etiqueta etiqueta){
        if (etiqueta == null)return null;
        EtiquetaResponseDTO etiquetaResponseDTO = new EtiquetaResponseDTO();
        etiquetaResponseDTO.setName(etiqueta.getName());
        return etiquetaResponseDTO;
    }

    public List<EtiquetaResponseDTO> toListEtiquetaResponseDTO(List<Etiqueta> etiquetas){
        if (etiquetas == null)return null;
        return etiquetas.stream().map(this::toEtiquetaResponseDTO).toList();
    }
}

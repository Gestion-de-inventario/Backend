package com.comedor.backend.application.common.mapper;

import com.comedor.backend.domain.model.Categoria;
import com.comedor.backend.domain.model.enums.Estado;
import com.comedor.backend.infrastructure.adapters.in.web.dto.request.CategoriaRequestDTO;
import com.comedor.backend.infrastructure.adapters.in.web.dto.response.CategoriaResponseDTO;
import lombok.Data;
import org.springframework.stereotype.Component;

import java.util.List;


@Component
public class CategoriaMapper {

    public Categoria toDomain(CategoriaRequestDTO categoriaRequestDTO)
    {
        if(categoriaRequestDTO == null) return null;
        Categoria categoria = new Categoria();
        categoria.setName(categoriaRequestDTO.getName());
        categoria.setStatus(Estado.ACTIVO);
        return categoria;
    }

    public CategoriaResponseDTO toCategoriaResponseDTO(Categoria categoria)
    {
        if(categoria == null) return null;
        CategoriaResponseDTO categoriaResponseDTO = new CategoriaResponseDTO();

        categoriaResponseDTO.setId(categoria.getId());
        categoriaResponseDTO.setName(categoria.getName());
        categoriaResponseDTO.setStatus(categoria.getStatus());
        return categoriaResponseDTO;
    }

    public List<CategoriaResponseDTO> toListCategoriaResponseDTO (List<Categoria> categorias)
    {
        if(categorias == null) return null;
        return categorias.stream().map(this::toCategoriaResponseDTO).toList();
    }
}
